package org.bailiun.multipleversionscoexist.en;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * <h2>DualMap — 简化版多值映射 / Simplified Multi-Value Map</h2>
 *
 * <p><b>中文说明：</b><br>
 * 这是一个对 {@link java.util.HashMap} 的轻量级扩展，主要用于一键式管理「一个键对应多个值」的场景。
 * 与传统的 <code>HashMap&lt;K, List&lt;V&gt;&gt;</code> 不同，<br>
 * 本类提供了更便捷的添加方法 {@link #put(Object, String)}，当指定的 key 不存在时会自动创建对应的 {@link java.util.List}。</p>
 *
 * <p><b>English Description:</b><br>
 * A lightweight extension of {@link java.util.HashMap} designed for scenarios where
 * one key maps to multiple values.
 * Unlike the standard <code>HashMap&lt;K, List&lt;V&gt;&gt;</code>, this class provides
 * a simplified {@link #put(Object, String)} method that automatically initializes
 * a new {@link java.util.List} when the key is not present.</p>
 *
 * <p><b>使用示例 / Example:</b></p>
 * <pre>{@code
 * DualMap<String> map = new DualMap<>();
 * map.put("version1", "api/user");
 * map.put("version1", "api/order");
 *
 * System.out.println(map.get("version1"));
 * // 输出: [api/user, api/order]
 * }</pre>
 *
 * <p><b>适用场景 / Use Cases:</b></p>
 * <ul>
 *   <li>多版本接口注册表（一个版本包含多个路径）</li>
 *   <li>一对多映射关系的临时缓存</li>
 *   <li>按分类聚合数据（例如：模块 -> 方法列表）</li>
 * </ul>
 *
 * @param <K> 映射键的类型 / The type of key maintained by this map
 * @author bailiun
 * @version 1.0.0
 * @since 1.0.0
 */
public class DualMap<K> extends HashMap<K, List<String>> {

    /**
     * <p><b>中文说明：</b>向指定的 key 对应的列表中添加一个 value。</p>
     * <p>如果 key 不存在，将自动创建一个新的 {@link java.util.ArrayList} 并存入。</p>
     *
     * <p><b>English Description:</b> Adds a new value to the {@link java.util.List}
     * associated with the given key. If the key does not exist, a new list is created
     * and stored automatically.</p>
     *
     * @param key 键 / The key under which the value will be added
     * @param value 要添加的值 / The value to be added to the list associated with the key
     * @return key 对应的完整列表（添加后的结果）/ The full list associated with the key after addition
     */
    public List<String> put(K key, String value) {
        List<String> list = this.computeIfAbsent(key, k -> new ArrayList<>());
        list.add(value);
        return list;
    }
}


