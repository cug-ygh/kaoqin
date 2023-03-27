// pages/login/login.js
const app = getApp()
Page({
  /**
   * 页面的初始数据
   */
  data: {
    disabled:false,
    no:'',
    pwd:'',
    noinput:false,
    pwdinput:false,
  },
  noinput:function(e){
    this.setData({no:e.detail.value});
    this.setData({noinput:true});
    if(this.data.noinput==true && this.data.pwdinput==true){
      this.setData({ disabled: false });
    }
 
  },
  pwdinput: function (e) {
    this.setData({ pwd: e.detail.value });
    this.setData({ pwdinput: true });
    if (this.data.noinput == true && this.data.pwdinput == true) {
      this.setData({ disabled: false });
    }
  },
  formSubmit: function (e) {
    wx.showLoading({
      title: '登录中...',
    })
    console.log(e);
    this.setData({ disabled: true});
    wx.request({
      url: 'http://login.kongw.work/login', 
      method:"POST",
      data: {
        username: e.detail.value.no,
        password: e.detail.value.pwd
      },
      header: {
        'content-type': 'application/x-www-form-urlencoded' 
      },
      success: function (res) {
        console.log(res);
        if(res.data==true) {
          console.log("登录成功")
          wx.showToast({
            title: "登录成功！",
            icon: 'success',
            duration: 1000
          })
          wx.navigateTo({
              url: '/pages/administration/admin',
          })
          } else {
            wx.showToast({
              title:"登录失败！",
              icon: 'none',
              duration: 2000
            })
          }
    }
    })
    // if(e.detail.value.no=='admin'&&e.detail.value.pwd=='admin'){
    //   console.log('登录成功！');
    //   wx.showToast({
    //      title: '登录成功！',
    //      icon: 'success',
    //      duration: 2000
    //   })
    //   setTimeout(function(){
    //     wx.navigateTo({
    //       url: '/pages/administration/admin',
    //     })},2000)
    // }
    // else{
    //   console.log('密码错误！');
    //   wx.showToast({
    //      title: '登录失败！',
    //      icon: 'none',
    //      duration: 2000
    //   })
    // }
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    
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
    if(this.data.no=='' || this.data.pwd==''){
      this.setData({ disabled: true });
    }else{
      this.setData({ disabled: false });
    }
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