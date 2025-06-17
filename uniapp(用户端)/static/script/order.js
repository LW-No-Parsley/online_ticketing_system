/**
 * 订单管理模块
 * 处理订单获取、筛选和详情查看逻辑
 */
import http from './http.js'

export default {
  name: 'OrderPage',
  data() {
    return {
      /**
       * 全部订单数据
       * @type {Array<Object>}
       */
      orders: [],
      /**
       * 当前显示的订单数据(根据筛选条件)
       * @type {Array<Object>}
       */
      filteredOrders: [],
      /**
       * 当前激活的订单状态标签
       * @type {string}
       * @default '全部'
       */
      activeTab: '全部',
    };
  },
  methods: {
    /**
     * 根据状态标签筛选订单
     * @param {string} tab - 订单状态标签('全部','待支付','未使用','退款/售后')
     */
    filterOrders(tab) {
      this.activeTab = tab;
      if (tab === '全部') {
        this.filteredOrders = this.orders;
      } else {
        this.filteredOrders = this.orders.filter(order => {
          if (tab === '待支付') return order.status === '待支付';
          if (tab === '未使用') return order.status === '未使用';
          if (tab === '退款/售后') return order.status === '退款' || order.status === '售后';
        });
      }
    },
    /**
     * 查看订单详情
     * @param {Object} order - 订单对象
     */
    viewDetails(order) {
      uni.setStorageSync('selectedOrder', order);
      uni.navigateTo({
        url: '/pages/order/details',
      });
    },
  },
  /**
   * 组件创建生命周期钩子
   * @async
   * @description 检查登录状态并获取用户订单数据
   */
  async created() {
    const userInfo = uni.getStorageSync('userInfo');
    if (!userInfo || !userInfo.username) {
      uni.showToast({
        title: '请先登录',
        icon: 'none',
      });
      setTimeout(() => {
        uni.navigateTo({
          url: '/pages/login/index',
        });
      }, 2000);
      return;
    }

    try {
      const data = await http.fetch(`http://localhost:8080/api/orders/details/${userInfo.username}`, {
        method: 'GET'
      });
      console.log(data);
      if (data.success) {
        this.orders = data.orders;
        this.filteredOrders = data.orders; // 默认显示全部订单
      } else {
        uni.showToast({
          title: data.message,
          icon: 'none',
        });
      }
    } catch (error) {
      console.error('获取订单失败:', error);
      uni.showToast({
        title: '获取订单失败，请重试',
        icon: 'none',
      });
    }
  },
};
