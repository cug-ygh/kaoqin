// pages/bujia_info/bujia_info.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    list:null
  },
  attached:function(){
    var bujia_Data=wx.getStorageSync('bujia_data');
    console.log("以下为申请考勤补假信息....")
    console.log(bujia_Data)
    this.setData({
      list:bujia_Data
    })
  },
  ready(){
    console.log(555)
  },
  
  handle:function(e){
    console.log("正在处理考勤补假信息")
    console.log("处理id："+e.currentTarget.id)
    var handle_info=e.currentTarget.id
    var handle_info_list=handle_info.split("+")
    wx.request({
      url: 'http://login.kongw.work/handle',
      data: {
        handle:handle_info_list[2],
        kq_num:handle_info_list[0],
        name:handle_info_list[1]
      },
      method:"POST",
      header:{
        'content-type':'application/x-www-form-urlencoded'
      },
      success: function (res) { 
        console.log("发送成功！")
        console.log(res.data)
      },
    })   
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var bujia_Data=wx.getStorageSync('bujia_data');
    console.log(bujia_Data)
    console.log("以下为申请考勤补假信息....")
    this.setData({
      list:bujia_Data
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