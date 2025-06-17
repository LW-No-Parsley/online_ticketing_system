/**
 * 首页主逻辑模块
 * 包含首页数据定义和导航方法
 */
import CustomTabbar from '@/components/CustomTabbar.uvue';
export default {
  components: { CustomTabbar },
  data() {
    return {
      // 首页轮播图图片路径数组
      headerImgs: [
        '/static/15504e23b35ed72b618c28b728938e9.jpg',
        '/static/4fa1274f11d0fea8434c8736fb5beea.jpg',
        '/static/0416e24704d4f7209e85f1417af9654.jpg'
      ]
    };
  },
  methods: {
    /**
     * 跳转到购票页面
     * @description 使用uni-app导航API跳转到购票页面
     */
    goTicket() {
      uni.navigateTo({ url: '/pages/ticket/index' });
    },
    /**
     * 跳转到年票参观登记页面 
     * @description 使用uni-app导航API跳转到年票页面
     */
    goYearTicket() {
      uni.navigateTo({ url: '/pages/year-ticket/index' });
    }
  }
};
