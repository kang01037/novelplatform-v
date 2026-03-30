package org.example.novelplatformv.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.novelplatformv.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 根据用户 ID 查询用户信息
     * @param userId 用户 ID
     * @return 用户对象，如果不存在则返回 null
     */
    User selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户对象，如果不存在则返回 null
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 查询所有用户列表
     * @return 用户列表
     */
    List<User> selectAllUsers();

    /**
     * 插入新用户
     * @param user 用户对象
     * @return 影响的行数，大于 0 表示插入成功
     */
    int insertUser(User user);

    /**
     * 更新用户信息
     * @param user 用户对象（需要包含 userId）
     * @return 影响的行数，大于 0 表示更新成功
     */
    int updateUser(User user);

    /**
     * 删除用户（物理删除）
     * @param userId 用户 ID
     * @return 影响的行数，大于 0 表示删除成功
     */
    int deleteUser(@Param("userId") Long userId);

    /**
     * 更新用户最后登录时间
     * @param userId 用户 ID
     * @return 影响的行数，大于 0 表示更新成功
     */
    int updateLastLoginTime(@Param("userId") Long userId);

    /**
     * 增加用户登录次数
     *
     * @param userId 用户 ID
     */
    void incrementLoginCount(@Param("userId") Long userId);


    User selectByUsernameWithoutDeleted(String username);


}
