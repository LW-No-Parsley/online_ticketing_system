/**
 * 统一请求工具
 * 封装uni.request和fetch API
 * 自动添加JWT鉴权头
 */

const http = {
  /**
   * 当前认证token
   * @type {string|null}
   * @description 从本地存储中获取或手动设置的用户认证token
   */
  token: uni.getStorageSync('userToken')?.token || null,
  
  /**
   * 设置认证token
   * @param {string} token - JWT认证令牌
   * @description 设置token并自动存储到本地存储，有效期15天
   */
  setToken(token) {
    this.token = token
    const expirationDate = new Date()
    expirationDate.setDate(expirationDate.getDate() + 15)
    uni.setStorageSync('userToken', {
      token,
      expires: expirationDate.toISOString()
    })
  },
  
  /**
   * uni-app请求封装方法
   * @param {Object} options - 请求配置对象
   * @param {string} options.url - 请求URL
   * @param {string} [options.method='GET'] - 请求方法(GET/POST/PUT/DELETE等)
   * @param {Object} [options.data] - 请求数据
   * @param {Object} [options.headers] - 自定义请求头
   * @returns {Promise} 返回Promise对象，成功时resolve响应数据，失败时reject错误对象
   */
  request(options) {
    const headers = {
      ...options.headers,
      'Content-Type': 'application/json'
    }
    
    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`
    }
    
    return new Promise((resolve, reject) => {
      uni.request({
        ...options,
        headers,
        success: (res) => {
          if (res.statusCode >= 200 && res.statusCode < 300) {
            resolve(res.data)
          } else {
            reject(res)
          }
        },
        fail: reject
      })
    })
  },
  
  /**
   * fetch API封装方法
   * @param {string} url - 请求URL
   * @param {Object} [options={}] - 请求配置对象
   * @param {string} [options.method='GET'] - 请求方法
   * @param {Object} [options.headers] - 自定义请求头
   * @param {Object} [options.body] - 请求体数据
   * @returns {Promise} 返回Promise对象，成功时resolve JSON响应数据
   * @throws {Error} 当HTTP状态码非2xx时抛出错误
   */
  fetch(url, options = {}) {
    const headers = {
      ...options.headers,
      'Content-Type': 'application/json'
    }
    
    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`
    }
    
    return fetch(url, {
      ...options,
      headers
    }).then(response => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      return response.json()
    })
  }
}

export default http
