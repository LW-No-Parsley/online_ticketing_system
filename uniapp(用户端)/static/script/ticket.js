/**
 * 票务管理模块
 * 处理票务查询、选择和订单提交逻辑
 */
import http from './http.js'

export default {
  name: 'TicketPage',
  data() {
    return {
      /**
       * 用户选择的日期
       * @type {string|null}
       */
      selectedDate: null,
      /**
       * 用户选择的时间索引
       * @type {number|null}
       */
      selectedTime: null,
      /**
       * 可选的日期列表
       * @type {Array<Object>}
       */
      dateOptions: [],
      /**
       * 可选的时间段列表(基于选择日期)
       * @type {Array<Object>}
       */
      timeOptions: [],
      /**
       * 从后端获取的原始票务数据
       * @type {Array<Object>}
       */
      allRawData: [],
      /**
       * 票种选项列表(成人票/儿童票等)
       * @type {Array<Object>}
       */
      ticketOptions: [],
    };
  },
  computed: {
    /**
     * 计算已选票务总数
     * @returns {number} 已选票务总数量
     */
    totalTickets() {
      return this.ticketOptions.reduce((sum, ticket) => sum + ticket.count, 0);
    },
  },
  methods: {
    /**
     * 通用选项获取方法
     * @async
     * @param {string} url - API地址
     * @returns {Promise<Array>} 返回选项数据
     */
    async fetchOptions(url) {
      try {
        const data = await http.fetch(url, { mode: 'cors' });
        return data;
      } catch (error) {
        console.error(`获取选项失败:`, error);
        return [];
      }
    },

    /**
     * 获取可选日期列表
     * @async
     * @description 从后端获取日期选项并处理为前端可用格式
     */
    async fetchDateOptions() {
      const rawData = await this.fetchOptions('http://localhost:8080/api/status');
      this.allRawData = rawData; // 保存所有数据备用

      const groupedByDate = rawData.reduce((acc, item) => {
        const remaining = item.totalTickets - item.soldTickets;
        if (!acc[item.date]) {
          acc[item.date] = {
            date: item.date,
            available: false,
            desc: new Date(item.date).toLocaleDateString('zh-CN', {
              month: 'numeric',
              day: 'numeric'
            }).replace(/\//g, '月') + '日',
            dayStatus: 0
          };
        }
        acc[item.date].available = acc[item.date].available || remaining > 0;
        return acc;
      }, {});
      this.dateOptions = Object.values(groupedByDate);
    },

    /**
     * 根据选择日期获取时间段选项
     * @description 从allRawData中筛选出对应日期的时间段
     */
    fetchTimeOptionsForSelectedDate() {
      if (!this.selectedDate || !this.allRawData.length) {
        this.timeOptions = [];
        return;
      }

      this.timeOptions = this.allRawData
        .filter(item => item.date === this.selectedDate)
        .map(item => {
          const remaining = item.totalTickets - item.soldTickets;
          return {
            label: item.time,
            available: remaining > 0,
            totalTickets: item.totalTickets,
            soldTickets: item.soldTickets,
            remainingTickets: remaining,
            status: remaining > 0 ? 1 : 2
          };
        });
    },

    /**
     * 选择日期处理
     * @param {number} idx - 日期选项索引
     */
    selectDate(idx) {
      const selectedOption = this.dateOptions[idx];
      if (!selectedOption || !selectedOption.available) {
        uni.showToast({
          title: '当天已约满',
          icon: 'none'
        });
        return;
      }
      this.selectedDate = selectedOption.date;
      this.selectedTime = null; // 清空已选时间
      this.fetchTimeOptionsForSelectedDate();
    },

    /**
     * 选择时间段处理
     * @param {number} idx - 时间段选项索引
     */
    selectTime(idx) {
      const selectedOption = this.timeOptions[idx];
      if (!selectedOption || !selectedOption.available) {
        uni.showToast({
          title: '当天已约满',
          icon: 'none'
        });
        return;
      }
      this.selectedTime = idx;
    },

    /**
     * 增加票种数量
     * @param {number} index - 票种索引
     */
    increaseTicket(index) {
      this.$set(this.ticketOptions[index], 'count', this.ticketOptions[index].count + 1);
    },
    /**
     * 减少票种数量
     * @param {number} index - 票种索引
     */
    decreaseTicket(index) {
      if (this.ticketOptions[index].count > 0) {
        this.$set(this.ticketOptions[index], 'count', this.ticketOptions[index].count - 1);
      }
    },

    /**
     * 是否显示票种选择区域
     * @returns {boolean} 当日期和时间都选择时返回true
     */
    showTicketOptions() {
      return this.selectedDate !== null && this.selectedTime !== null;
    },

    /**
     * 获取票种选项
     * @async
     * @description 从后端获取票种类型(成人票/儿童票等)
     */
    async fetchTicketOptions() {
      try {
        const data = await http.fetch('http://localhost:8080/api/ticket-types', { mode: 'cors' });
        this.ticketOptions = data.map(ticket => ({ ...ticket, count: 0 }));
      } catch (error) {
        console.error('获取票种信息失败:', error);
      }
    },

    /**
     * 提交订单处理
     * @async
     * @description 检查登录状态后提交订单数据到后端
     * @throws {Error} 提交失败时抛出错误
     */
    async goToOrderPage() {
      const userInfo = uni.getStorageSync('userInfo');
      if (!userInfo) {
        uni.showToast({
          title: '请先登录',
          icon: 'none',
        });
        setTimeout(() => {
          uni.navigateTo({
            url: '/pages/login/index',
          });
        }, 3000);
        return; // 确保未登录时立即返回
      }

      const ticketOptions = this.ticketOptions.filter(ticket => ticket.count > 0);
      const payload = {
        username: userInfo.username,
        selectedDate: this.selectedDate,
        selectedTime: this.timeOptions[this.selectedTime]?.label,
        tickets: ticketOptions,
        totalAmount: ticketOptions.reduce((sum, ticket) => sum + ticket.count * ticket.price, 0),
      };

      try {
        const data = await http.fetch('http://localhost:8080/api/orders/submit-order', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(payload),
        });

        if (data.success) {
          uni.setStorageSync('ticketOptions', ticketOptions);
          uni.showToast({
            title: '订单提交成功',
            icon: 'success',
            duration: 2000,
            success: () => {
              setTimeout(() => {
                this.fetchOrderDetails(); // 刷新订单详情
              uni.switchTab({
                url: '/pages/order/index',
              });
              }, 2000);
              
            },
          });
        } else {
          uni.showToast({
            title: data.message || '提交订单失败',
            icon: 'none',
          });
        }
      } catch (error) {
        console.error('提交订单失败:', error);
        uni.showToast({
          title: '提交订单失败，请重试',
          icon: 'none',
        });
      }
    },

  //   async fetchOrderDetails() {
  //     try {
  //       const response = await fetch('http://localhost:8080/api/orders/get-order-details', { mode: 'cors' });
  //       if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
  //       const data = await response.json();
  //       console.log('订单详情:', data);
  //       // 在这里处理订单详情数据
  //     } catch (error) {
  //       console.error('获取订单详情失败:', error);
  //     }
  //   },
  },

  /**
   * 页面加载生命周期钩子
   * @description 初始化日期和票种选项
   */
  onLoad() {
    this.fetchDateOptions();
    this.fetchTicketOptions(); // 加载票种信息
  }
};
