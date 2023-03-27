var dateTimePicker = require('../../utils/dateTimePicker.js');

Page({
  data: {
    date: '2018-10-01',
    time: '12:00',
    startYear: 2000,
    endYear: 2050,
    dateTimeArray: null,
    dateTime: null,
    dateTimeArray1: null,
    dateTime1: null,
    if_select:false,
    kaoqin_num:null
  },
  onLoad(){
    // 获取完整的年月日 时分秒，以及默认显示的数组
    var obj = dateTimePicker.dateTimePicker(this.data.startYear, this.data.endYear);
    var obj1 = dateTimePicker.dateTimePicker(this.data.startYear, this.data.endYear);
    // 精确到分的处理，将数组的秒去掉
    var lastArray = obj1.dateTimeArray.pop();
    var lastTime = obj1.dateTime.pop();
    
    this.setData({
      dateTime: obj.dateTime,
      dateTimeArray: obj.dateTimeArray,
      dateTimeArray1: obj1.dateTimeArray,
      dateTime1: obj1.dateTime,
    });
  },
  changeDate(e){
    this.setData({ date:e.detail.value});
  },
  changeTime(e){
    this.setData({ time: e.detail.value });
  },
  changeDateTime(e){
    this.setData({ dateTime: e.detail.value });
  },
  changeDateTime1(e) {
    this.setData({ dateTime1: e.detail.value });
  },
  changeDateTimeColumn(e){
    var arr = this.data.dateTime, dateArr = this.data.dateTimeArray;

    arr[e.detail.column] = e.detail.value;
    dateArr[2] = dateTimePicker.getMonthDay(dateArr[0][arr[0]], dateArr[1][arr[1]]);

    this.setData({
      dateTimeArray: dateArr,
      dateTime: arr
    });
  },
  changeDateTimeColumn1(e) {
    var arr = this.data.dateTime1, dateArr = this.data.dateTimeArray1;

    arr[e.detail.column] = e.detail.value;
    dateArr[2] = dateTimePicker.getMonthDay(dateArr[0][arr[0]], dateArr[1][arr[1]]);

    this.setData({ 
      dateTimeArray1: dateArr,
      dateTime1: arr
    });
  },
  upfile(e){
    wx.chooseMessageFile({
      count: 1,
      type:File,
      success(res){
        const tempFilesPaths=res.tempFilesPaths
        Console.log('选择',res)
      }
    })
  },
  gotopage(){
    wx.navigateTo({
      url: '/pages/upfile/upfile',
    })
  },
  //通过判断改变状态时value值是否为空来来判断是否被选中 
//有值代表选中   为空代表没有选中 
if_select:function(e){
  if (e.detail.value =='') {
  console.log('用户不自定义时间')
  this.setData({if_select:false})
  }
  else {
  console.log('用户自定义时间')   //表示选中状态
  this.setData({if_select:true})
  }
  },
  result:function(e){
    console.log('正在处理结果...')
    var start_time = new Array()
    start_time[0]=this.data.dateTime[3]
    start_time[1]=this.data.dateTime[4]

    var end_time = new Array()
    end_time[0]=this.data.dateTime1[3]
    end_time[1]=this.data.dateTime1[4]

    var that=this
    console.log(start_time)
    console.log(end_time)

    wx.request({
      url: 'http://login.kongw.work/file/inside',
      data: {
        starttime_h:start_time[0],
        starttime_m:start_time[1],
        endtime_h:end_time[0],
        endtime_m:end_time[1],
        choose:this.data.if_select
      },
      method:"GET",
      header:{
        'content-type':'application/json'
      },
      success: function (res) {
        console.log("查看考勤记录")
        console.log(res.data.status)
        that.setData({ kaoqin_num:res.data.kq});
        if(res.data.status=="no_file"){
        wx.showToast({
          title:"请先上传文件",
          icon: 'none',
          duration: 2000
        })
      }
      else {
        wx.showModal({
          title: '处理成功',
          content: '生成的考勤号为：'+res.data.kq+"  点击确定即可复制该考勤号",
          success: function(res) {
            if (res.confirm) {
            console.log('用户点击确定')
            wx.setClipboardData({
              //准备复制的数据
              data: that.data.kaoqin_num,
              success: function (res) {
                wx.showToast({
                  title: '复制成功',
                });
              }
            })
            } else if (res.cancel) {
            console.log('用户点击取消')
            }
          }
        })}
        // var result_data=res.data
        // wx.setStorageSync('result_data', result_Data)
      },
    })
  },
  bujia(){
     //连接服务器，查询数据库
     wx.request({
       url: 'http://login.kongw.work/check_apply',
       data: {
       },
       method:"POST",
       header:{
         'content-type':'application/json'
       },
       success: function (res) {
         //console.log(res.data)
         var bujia_data=res.data
         wx.setStorageSync('bujia_data', bujia_data)
         wx.navigateTo({
          url: '/pages/bujia_info/bujia_info',
        })
       },
     })    
  },
  tongji:function(e){
    wx.request({
      url: 'http://login.kongw.work/check_total',
      data: {
      },
      method:"POST",
      header:{
        'content-type':'application/json'
      },
      success: function (res) {
        console.log(res.data)
        var tongji_data=res.data
         wx.setStorageSync('tongji_data', tongji_data)
         wx.navigateTo({
          url: '/pages/Statistics/Statistics',
        })
      },
    })
  }
})
