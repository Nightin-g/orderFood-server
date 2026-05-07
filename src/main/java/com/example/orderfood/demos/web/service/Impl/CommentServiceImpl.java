package com.example.orderfood.demos.web.service.Impl;

import com.example.orderfood.demos.web.DTO.CommentCreateDTO;
import com.example.orderfood.demos.web.exception.BusinessException;
import com.example.orderfood.demos.web.mapper.CommentMapper;
import com.example.orderfood.demos.web.mapper.DishMapper;
import com.example.orderfood.demos.web.mapper.ShopMapper;
import com.example.orderfood.demos.web.model.Comment;
import com.example.orderfood.demos.web.model.Dish;
import com.example.orderfood.demos.web.model.Shop;
import com.example.orderfood.demos.web.service.CommentService;
import com.example.orderfood.demos.web.util.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);
    
    private static final String SHOP_SCORE_PREFIX = "shop:score:";
    private static final String DISH_SCORE_PREFIX = "dish:score:";
    private static final long CACHE_EXPIRATION = 1L; // 缓存过期时间（小时）

    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private DishMapper dishMapper;
    
    @Autowired
    private ShopMapper shopMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // 本地缓存，用于存储待更新的评分
    private final Map<BigInteger, BigDecimal> dishScoreCache = new ConcurrentHashMap<>();
    private final Map<BigInteger, Integer> dishScorePersonCache = new ConcurrentHashMap<>();
    private final Map<BigInteger, BigDecimal> shopScoreCache = new ConcurrentHashMap<>();
    private final Map<BigInteger, Integer> shopScorePersonCache = new ConcurrentHashMap<>();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R createComment(CommentCreateDTO commentCreateDTO) {
        // 1. 获取当前登录用户的ID
        BigInteger userId = getCurrentUserId();
        
        // 2. 创建评论对象
        Comment comment = new Comment();
        comment.setContext(commentCreateDTO.getContext());
        comment.setScore(commentCreateDTO.getScore());
        comment.setShopId(commentCreateDTO.getShopId());
        comment.setUserId(userId);
        comment.setDishId(commentCreateDTO.getDishId());
        comment.setCreateTime(LocalDateTime.now());
        
        // 3. 保存评论
        int result = commentMapper.insert(comment);
        if (result <= 0) {
            logger.error("评论创建失败，commentCreateDTO: {}", commentCreateDTO);
            throw new BusinessException(500, "评论创建失败，请稍后重试");
        }
        
        // 4. 更新缓存中的评分和评分人数
        updateScoreCache(commentCreateDTO);
        
        logger.info("评论创建成功，commentId: {}", comment.getCommentId());
        return R.success("评论创建成功", comment);
    }

    @Override
    public List<Comment> getShopComments(BigInteger shopId) {
        return commentMapper.selectByShopId(shopId);
    }

    @Override
    public List<Comment> getDishComments(BigInteger dishId) {
        return commentMapper.selectByDishId(dishId);
    }

    @Override
    public List<Comment> getUserComments() {
        // 获取当前登录用户的ID
        BigInteger userId = getCurrentUserId();
        return commentMapper.selectByUserId(userId);
    }

    @Override
    public BigDecimal getShopAverageScore(BigInteger shopId) {
        // 先从本地缓存中获取
        if (shopScoreCache.containsKey(shopId)) {
            return shopScoreCache.get(shopId);
        }
        
        // 从Redis中获取
        String redisKey = SHOP_SCORE_PREFIX + shopId;
        BigDecimal score = (BigDecimal) redisTemplate.opsForValue().get(redisKey);
        if (score != null) {
            shopScoreCache.put(shopId, score);
            return score;
        }
        
        // 从数据库中获取
        score = commentMapper.selectAverageScoreByShopId(shopId);
        score = score != null ? score : BigDecimal.ZERO;
        
        // 更新缓存
        shopScoreCache.put(shopId, score);
        redisTemplate.opsForValue().set(redisKey, score, CACHE_EXPIRATION, TimeUnit.HOURS);
        
        return score;
    }

    @Override
    public BigDecimal getDishAverageScore(BigInteger dishId) {
        // 先从本地缓存中获取
        if (dishScoreCache.containsKey(dishId)) {
            return dishScoreCache.get(dishId);
        }
        
        // 从Redis中获取
        String redisKey = DISH_SCORE_PREFIX + dishId;
        BigDecimal score = (BigDecimal) redisTemplate.opsForValue().get(redisKey);
        if (score != null) {
            dishScoreCache.put(dishId, score);
            return score;
        }
        
        // 从数据库中获取
        score = commentMapper.selectAverageScoreByDishId(dishId);
        score = score != null ? score : BigDecimal.ZERO;
        
        // 更新缓存
        dishScoreCache.put(dishId, score);
        redisTemplate.opsForValue().set(redisKey, score, CACHE_EXPIRATION, TimeUnit.HOURS);
        
        return score;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R deleteComment(BigInteger commentId) {
        // 1. 查询评论
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(404, "评论不存在");
        }
        
        // 2. 验证评论是否属于当前用户
        BigInteger userId = getCurrentUserId();
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权删除该评论");
        }
        
        // 3. 删除评论
        int result = commentMapper.deleteById(commentId);
        if (result <= 0) {
            logger.error("删除评论失败，commentId: {}", commentId);
            throw new BusinessException(500, "删除评论失败，请稍后重试");
        }
        
        // 4. 清除相关缓存
        clearCommentCache(comment);
        
        logger.info("评论删除成功，commentId: {}", commentId);
        return R.success("评论删除成功");
    }
    
    /**
     * 更新缓存中的评分和评分人数
     */
    private void updateScoreCache(CommentCreateDTO commentCreateDTO) {
        // 更新店铺评分
        if (commentCreateDTO.getShopId() != null) {
            updateShopScoreCache(commentCreateDTO.getShopId(), commentCreateDTO.getScore());
        }
        
        // 更新菜品评分
        if (commentCreateDTO.getDishId() != null) {
            updateDishScoreCache(commentCreateDTO.getDishId(), commentCreateDTO.getScore());
        }
    }
    
    /**
     * 更新店铺评分缓存
     */
    private void updateShopScoreCache(BigInteger shopId, BigDecimal score) {
        try {
            // 计算新的平均评分
            BigDecimal currentScore = getShopAverageScore(shopId);
            int currentPerson = shopScorePersonCache.getOrDefault(shopId, 0);
            
            // 从数据库获取当前评分人数
            if (currentPerson == 0) {
                Shop shop = shopMapper.selectById(shopId);
                if (shop != null && shop.getShopScorePerson() != null) {
                    currentPerson = shop.getShopScorePerson();
                }
            }
            
            // 计算新的平均评分
            BigDecimal newScore = calculateNewScore(currentScore, currentPerson, score);
            
            // 更新缓存
            shopScoreCache.put(shopId, newScore);
            shopScorePersonCache.put(shopId, currentPerson + 1);
            redisTemplate.opsForValue().set(SHOP_SCORE_PREFIX + shopId, newScore, CACHE_EXPIRATION, TimeUnit.HOURS);
        } catch (Exception e) {
            logger.error("更新店铺评分缓存时发生异常，shopId: {}", shopId, e);
        }
    }
    
    /**
     * 更新菜品评分缓存
     */
    private void updateDishScoreCache(BigInteger dishId, BigDecimal score) {
        try {
            // 计算新的平均评分
            BigDecimal currentScore = getDishAverageScore(dishId);
            int currentPerson = dishScorePersonCache.getOrDefault(dishId, 0);
            
            // 从数据库获取当前评分人数
            if (currentPerson == 0) {
                Dish dish = dishMapper.selectById(dishId);
                if (dish != null && dish.getDishScorePerson() != null) {
                    currentPerson = dish.getDishScorePerson();
                }
            }
            
            // 计算新的平均评分
            BigDecimal newScore = calculateNewScore(currentScore, currentPerson, score);
            
            // 更新缓存
            dishScoreCache.put(dishId, newScore);
            dishScorePersonCache.put(dishId, currentPerson + 1);
            redisTemplate.opsForValue().set(DISH_SCORE_PREFIX + dishId, newScore, CACHE_EXPIRATION, TimeUnit.HOURS);
        } catch (Exception e) {
            logger.error("更新菜品评分缓存时发生异常，dishId: {}", dishId, e);
        }
    }
    
    /**
     * 计算新的平均评分
     */
    private BigDecimal calculateNewScore(BigDecimal currentScore, int currentPerson, BigDecimal newScore) {
        if (currentPerson == 0) {
            return newScore;
        }
        return currentScore.multiply(new BigDecimal(currentPerson))
                .add(newScore)
                .divide(new BigDecimal(currentPerson + 1), 1, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 清除评论相关缓存
     */
    private void clearCommentCache(Comment comment) {
        try {
            if (comment.getShopId() != null) {
                shopScoreCache.remove(comment.getShopId());
                redisTemplate.delete(SHOP_SCORE_PREFIX + comment.getShopId());
            }
            if (comment.getDishId() != null) {
                dishScoreCache.remove(comment.getDishId());
                redisTemplate.delete(DISH_SCORE_PREFIX + comment.getDishId());
            }
        } catch (Exception e) {
            logger.error("清除评论缓存时发生异常", e);
        }
    }
    
    /**
     * 获取当前登录用户的ID
     */
    private BigInteger getCurrentUserId() {
        Long userIdLong = (Long) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return BigInteger.valueOf(userIdLong);
    }
    
    /**
     * 定时任务：每5分钟更新数据库中的评分和评分人数
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    @Transactional(rollbackFor = Exception.class)
    public void updateScoresToDatabase() {
        try {
            logger.info("开始执行评分更新定时任务");
            
            // 更新店铺评分
            updateShopScoresToDatabase();
            
            // 更新菜品评分
            updateDishScoresToDatabase();
            
            // 清空缓存
            clearLocalCache();
            
            logger.info("评分更新定时任务执行完成");
        } catch (Exception e) {
            logger.error("执行评分更新定时任务时发生异常", e);
        }
    }
    
    /**
     * 更新店铺评分到数据库
     */
    private void updateShopScoresToDatabase() {
        for (Map.Entry<BigInteger, BigDecimal> entry : shopScoreCache.entrySet()) {
            BigInteger shopId = entry.getKey();
            BigDecimal score = entry.getValue();
            Integer person = shopScorePersonCache.getOrDefault(shopId, 0);
            
            try {
                // 更新数据库
                Shop shop = shopMapper.selectById(shopId);
                if (shop != null) {
                    shop.setShopScore(score);
                    shop.setShopScorePerson(person);
                    shopMapper.updateById(shop);
                }
            } catch (Exception e) {
                logger.error("更新店铺评分到数据库时发生异常，shopId: {}", shopId, e);
            }
        }
    }
    
    /**
     * 更新菜品评分到数据库
     */
    private void updateDishScoresToDatabase() {
        for (Map.Entry<BigInteger, BigDecimal> entry : dishScoreCache.entrySet()) {
            BigInteger dishId = entry.getKey();
            BigDecimal score = entry.getValue();
            Integer person = dishScorePersonCache.getOrDefault(dishId, 0);
            
            try {
                // 更新数据库
                Dish dish = dishMapper.selectById(dishId);
                if (dish != null) {
                    dish.setDishScore(score);
                    dish.setDishScorePerson(person);
                    dishMapper.updateById(dish);
                }
            } catch (Exception e) {
                logger.error("更新菜品评分到数据库时发生异常，dishId: {}", dishId, e);
            }
        }
    }
    
    /**
     * 清空本地缓存
     */
    private void clearLocalCache() {
        shopScoreCache.clear();
        shopScorePersonCache.clear();
        dishScoreCache.clear();
        dishScorePersonCache.clear();
    }
}
