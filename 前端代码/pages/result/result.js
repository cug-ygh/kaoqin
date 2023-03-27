// pages/result/result.js
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
    search:function(e){
      var user_kq_num=e.detail.value.kaoqin__input;
      var username=e.detail.value.name__input;
      wx.request({
        url: 'http://login.kongw.work/check_kq',
        data: {
          name:username,
          kq_num:user_kq_num
        },
        header:{
          'content-type':'application/json'
        },
        success: function (res) {
          console.log(res.data)
          if(res.data=='0'){
            wx.showToast({
              title: '提交待审核！',
              icon: 'loading',
              duration: 3000
           })
          }
          if(res.data=='1'){
            wx.showToast({
              title: '提交通过！',
              icon: 'success',
              duration: 3000
           })
          }
          if(res.data=='2'){
            wx.showToast({
              title: '提交被拒绝！',
              icon: 'loading',
              duration: 3000
           })
          }
          if(res.data=='-1'){
            wx.showToast({
              title: '未找到对应提交！',
              icon: 'loading',
              duration: 3000
           })
          }
        },
      })   

    }  //查询考勤申请结果

  }
})
