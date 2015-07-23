package org.redisson;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.redisson.core.RBucket;
import org.redisson.core.RMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

public class RedissonBucketTest extends BaseTest {

    @Test
    public void testFindKeys() {
        RBucket<String> bucket = redisson.getBucket("test1");
        bucket.set("someValue");
        RMap<String, String> map = redisson.getMap("test2");
        map.fastPut("1", "2");

        Queue<String> keys = redisson.findKeys("test?");
        MatcherAssert.assertThat(keys, Matchers.contains("test1", "test2"));

        Queue<String> keys2 = redisson.findKeys("test");
        MatcherAssert.assertThat(keys2, Matchers.empty());
    }

    @Test
    public void testMassDelete() {
        RBucket<String> bucket = redisson.getBucket("test");
        bucket.set("someValue");
        RMap<String, String> map = redisson.getMap("map2");
        map.fastPut("1", "2");

        Assert.assertEquals(2, redisson.delete("test", "map2"));
        Assert.assertEquals(0, redisson.delete("test", "map2"));
    }

    @Test
    public void testRenamenx() {
        RBucket<String> bucket = redisson.getBucket("test");
        bucket.set("someValue");
        RBucket<String> bucket2 = redisson.getBucket("test2");
        bucket2.set("someValue2");
        Assert.assertTrue(bucket.renamenx("test1"));
        RBucket<String> oldBucket = redisson.getBucket("test");
        Assert.assertNull(oldBucket.get());
        RBucket<String> newBucket = redisson.getBucket("test1");
        Assert.assertEquals("someValue", newBucket.get());
        Assert.assertFalse(newBucket.renamenx("test2"));
    }

    @Test
    public void testRename() {
        RBucket<String> bucket = redisson.getBucket("test");
        bucket.set("someValue");
        bucket.rename("test1");
        RBucket<String> oldBucket = redisson.getBucket("test");
        Assert.assertNull(oldBucket.get());
        RBucket<String> newBucket = redisson.getBucket("test1");
        Assert.assertEquals("someValue", newBucket.get());
    }

    @Test
    public void testSetGet() {
        RBucket<String> bucket = redisson.getBucket("test");
        Assert.assertNull(bucket.get());
        String value = "somevalue";
        bucket.set(value);
        Assert.assertEquals(value, bucket.get());
    }

    @Test
    public void testSetDelete() {
        RBucket<String> bucket = redisson.getBucket("test");
        String value = "somevalue";
        bucket.set(value);
        Assert.assertEquals(value, bucket.get());
        Assert.assertTrue(bucket.delete());
        Assert.assertNull(bucket.get());
        Assert.assertFalse(bucket.delete());
    }


    @Test
    public void testSetExist() {
        RBucket<String> bucket = redisson.getBucket("test");
        Assert.assertNull(bucket.get());
        String value = "somevalue";
        bucket.set(value);
        Assert.assertEquals(value, bucket.get());

        Assert.assertTrue(bucket.exists());
    }

    @Test
    public void testSetDeleteNotExist() {
        RBucket<String> bucket = redisson.getBucket("test");
        Assert.assertNull(bucket.get());
        String value = "somevalue";
        bucket.set(value);
        Assert.assertEquals(value, bucket.get());

        Assert.assertTrue(bucket.exists());

        bucket.delete();

        Assert.assertFalse(bucket.exists());
    }

    @Test
    public void testGetPattern() {
        Collection<String> names = Arrays.asList("test:testGetPattern:one", "test:testGetPattern:two");
        Collection<String> vals = Arrays.asList("one-val", "two-val");
        redisson.getBucket("test:testGetPattern:one").set("one-val");
        redisson.getBucket("test:testGetPattern:two").set("two-val");
        List<RBucket<String>> buckets = redisson.getBuckets("test:testGetPattern:*");
        Assert.assertEquals(2, buckets.size());
        Assert.assertTrue(names.contains(buckets.get(0).getName()));
        Assert.assertTrue(names.contains(buckets.get(1).getName()));
        Assert.assertTrue(vals.contains(buckets.get(0).get()));
        Assert.assertTrue(vals.contains(buckets.get(1).get()));
        for (RBucket<String> bucket : buckets) {
            bucket.delete();
        }
    }
}
