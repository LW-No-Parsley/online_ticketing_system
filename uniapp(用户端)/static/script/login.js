/**
 * 登录模块
 * 处理用户登录、密码加密和认证逻辑
 */
import http from './http.js'

export default {
  data() {
    return {
      // 用户名输入
      username: '',
      // 密码输入
      password: ''
    };
  },
  methods: {
    /**
     * 处理用户登录
     * @async
     * @description 执行以下流程：
     * 1. 验证输入
     * 2. 密码SHA-256加密
     * 3. 发送登录请求
     * 4. 处理响应结果
     * 5. 存储token和用户信息
     * @throws {Error} 网络请求失败时抛出错误
     */
    async handleLogin() {
      if (!this.username || !this.password) {
        uni.showToast({
          title: '请输入用户名和密码',
          icon: 'none'
        });
        return;
      }

      // 密码加密 - 使用Web Crypto API进行SHA-256哈希
      // 注意：这仅是前端加密，后端仍需进行密码验证
      const encoder = new TextEncoder()
      const data = encoder.encode(this.password)
      const hashBuffer = await crypto.subtle.digest('SHA-256', data)
      const hashArray = Array.from(new Uint8Array(hashBuffer))
      const encryptedPassword = hashArray.map(b => b.toString(16).padStart(2, '0')).join('')
      
      try {
        const res = await http.request({
          url: 'http://localhost:8080/api/user/login',
          method: 'POST',
          data: {
            username: this.username,
            password: encryptedPassword
          }
        });

        if (res.success) {
          uni.showToast({
            title: res.message,
            icon: 'success'
          });
          // 存储token和用户信息
          http.setToken(res.token);
          uni.setStorageSync('userToken', {
            ...uni.getStorageSync('userToken'),
            username: this.username
          });
          // 打印存储的token信息以调试
          console.log('登录成功后存储的token信息:', uni.getStorageSync('userToken'));
          // 延时3秒后返回上一页
          setTimeout(() => {
            uni.navigateBack({ delta: 1 });
          }, 3000);
        } else {
          uni.showToast({
            title: res.message || '登录失败',
            icon: 'none'
          });
        }
      } catch (error) {
        uni.showToast({
          title: '网络错误，请稍后重试',
          icon: 'none'
        });
        console.error('登录请求失败:', error);
      }
    },
    /**
     * 跳转到注册页面
     * @description 使用uni-app导航API跳转到注册页面
     */
    handleRegister() {
      uni.navigateTo({
        url: '/pages/login/register'
      });
    }
  }
};
