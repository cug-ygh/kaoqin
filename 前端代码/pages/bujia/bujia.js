// pages/bujia/bujia.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {

  },

  /**
   * 组件的初始数据
   */
  data: {

  },

  /**
   * 组件的方法列表
   */
  methods: {
    bindTextAreaBlur: function(e) {
      console.log(e.detail.value);
      var that = this;
      that.setData({
        details: e.detail.value
      });
    },
    bujia:function(e){
      var user_kq_num=e.detail.value.kaoqin__input;
      var username=e.detail.value.name__input;
      var userdetail=e.detail.value.detailText;
      wx.request({
        url: 'http://login.kongw.work/insert',
        data: {
          name:username,
          kq_num:user_kq_num,
          reason:userdetail
        },
        header:{
          'content-type':'application/json'
        },
        success: function (res) {
          console.log(res.data)
          wx.showToast({
            title: '提交成功！',
            icon: 'success',
            duration: 1000
         })
        },
      })
    }
  }
})
