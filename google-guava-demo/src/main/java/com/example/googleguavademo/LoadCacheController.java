package com.example.googleguavademo;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/MemCacheLoad")
public class LoadCacheController {

    private static LoadingCache<String, String> numCache = CacheBuilder.newBuilder().
            expireAfterWrite(5L, TimeUnit.MINUTES).
            maximumSize(5000L).
            build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws Exception {
                    System.out.println("no cache");
                    return key;
                }
            });

    @GetMapping("getMemCache")
    public String getMemCache(String key) {
        try {
            return numCache.get(key, () -> loader());
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }

    @GetMapping("putMemCache")
    public void putMemCache(String key, String content) {
        numCache.put(key, content);
    }

    @GetMapping("putAllMemCache")
    public void putAllMemCache(String key, String content) {
        Map<String, String> map = new HashMap<>();
        map.put(key, content);
        map.put(key + "1", content + "1");
        map.put(key + "2", content + "2");
        numCache.putAll(map);
    }

    @GetMapping("getIfMemCache")
    public String getIfMemCache(String key) {
        return numCache.getIfPresent(key);
    }

    @GetMapping("getAllMemCache")
    public String getAllMemCache(String key) {
        List<String> list = new ArrayList<>();
        list.add(key);
        list.add(key + "1");
        list.add(key + "2");
        Iterable<String> iterable = list;
        return String.valueOf(numCache.getAllPresent(iterable));
    }

    @GetMapping("invalidateAll")
    public void invalidateAll() {
        numCache.invalidateAll();
    }

    @GetMapping("invalidate")
    public void invalidate(String key) {
        numCache.invalidate(key);
    }

    static String loader() {
        System.out.println("threa:" + Thread.currentThread() + ",??????????????????");
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int i = ThreadLocalRandom.current().nextInt();
        System.out.println("threa:" + Thread.currentThread() + ",????????????????????????" + i);
        return String.valueOf(i);
    }
}
