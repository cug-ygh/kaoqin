// pages/Statistics/Statistics.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    late_total:null, 
    noab_total:null,
    leave_total:null, 
    leave_per:null, 
    noab_per:null, 
    late_per:null
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var tongji_Data=wx.getStorageSync('tongji_data');
    console.log(tongji_Data)
    this.setData({
      late_total:tongji_Data.late_total, 
      noab_total:tongji_Data.noab_total,
      leave_total:tongji_Data.leave_total, 
      leave_per:tongji_Data.leave_per, 
      noab_per:tongji_Data.noab_per, 
      late_per:tongji_Data.late_per
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})