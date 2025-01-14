package com.li.bookworm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.bookworm.constants.CacheKeyConstants;
import com.li.bookworm.model.Group;
import com.li.bookworm.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, Group> getGroupsFromCache() {
        try {
            Object cacheResponse = redisTemplate.opsForValue().get(CacheKeyConstants.GROUP_ALL_CACHE_KEY);
            if (cacheResponse instanceof Map) {
                Map<String, Object> cacheValue = (Map<String, Object>) cacheResponse;
                Map<String, Group> groups = new HashMap<>();
                cacheValue.forEach((groupName, group) -> groups.put(groupName, convertToGroups(group)));
                return groups;
            }
            else {
                return new HashMap<>();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public void updateGroupCache(Group group, String action) {
        Map<String, Group> groupsFromCache = getGroupsFromCache();
        if (groupsFromCache != null && !groupsFromCache.isEmpty()) {
            if (action.equals("DELETE")) {
                groupsFromCache.remove(group.getName());
            }
            else groupsFromCache.put(group.getName(), group);
            redisTemplate.opsForValue().set(CacheKeyConstants.GROUP_ALL_CACHE_KEY, groupsFromCache);
        }
    }

    private Group convertToGroups(Object cacheValue) {
        if (cacheValue instanceof LinkedHashMap) {
            return objectMapper.convertValue(cacheValue, Group.class);
        }
        return null;
    }

}
