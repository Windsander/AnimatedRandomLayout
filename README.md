# AnimatedRandomLayout

          本布局实现了在屏幕上随机生成可供操作的子控件控件，并完成向中心移动的随控件出现位置，
      动态设定的动画效果。
      
#<h3>SimpleExample:
<img src="http://i11.tietuku.com/96c85ac6edc5e00b.gif" align="center">
      
#<h3>可操控参数：

      随机生成的周期                  setLooperDuration(int mLooperDuration)
      最大动画时长                    setDefaultDruation(int mDefaultDruation)
      同一时刻随机生成控件的最大个数  setItemShowCount(int itemShowCount)
      随机控件分布网格空间大小        setRegularity(int xRegularity, int yRgularity)
      随机控件总数和显示（类似Adapter）setOnCreateItemViewListener(OnCreateItemViewListener itemViewListener)

#<h3>子控件特点：
      
      对于子控件没有确切的要求，只要是控件父类为 View 类，就可以使用本随机布局添加显示。

#<h3>注意：

      如果期望修改动画，可以先自定义动画，随后修改com.special.simplecloudview.random_layout
      文档中CloudRandomLayout.java（即，布局的所在文件）的startZoomAnimation方法即可。

