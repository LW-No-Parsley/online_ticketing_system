import CustomTabbar from '../../components/CustomTabbar.uvue';
export default {
  components: { CustomTabbar },
  data() {
    return {
      username: '' // 添加 username 属性以解决警告
    };
  },
  created() {
    const userInfo = uni.getStorageSync('userInfo');
    if (userInfo && userInfo.username) {
      this.username = userInfo.username;
    }
  },
  methods: {
    goLogin() {
      uni.navigateTo({
        url: '/pages/login/index',
        events: {
          refreshProfile: () => {
            // 刷新用户信息
            const userInfo = uni.getStorageSync('userInfo');
            this.username = userInfo.username;
          }
        }
      });
    },
    logout() {
      // 清除本地存储并刷新页面
      uni.removeStorageSync('userInfo');
      this.username = '';
      uni.showToast({ title: '已退出登录', icon: 'none' });
      // 验证清除是否成功
      console.log('退出登录后 userInfo:', uni.getStorageSync('userInfo'));
      // 确保 username 被正确清空
      this.username = '';
      console.log('退出登录后清空的 username:', this.username);
      // 强制刷新页面以更新模板
      this.$forceUpdate();
    }
  }
};