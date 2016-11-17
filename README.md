#封装了RecyclerView的适配器（下拉、加载更多、设置空布局）


##因为在github上找了很多，感觉要么功能太多，要么不实用。此次封装是基于我们项目来的，如果各位需要下拉刷新、加载更多、设置空布局，让每个Item独立，在项目里相同的Item只想写一个等等，那么请往下看...

##使用方法
### 1、在工程下的build.gradle里面添加
 
  
	allprojects {
	    repositories {
	        jcenter()
	        maven { url "https://jitpack.io" }
	    }
	}
### 2、在项目下build.gradle里面添加
	 
	compile 'com.github.luoxiong94:base-adapter:1.0'
	 




该项目参考了以下项目：

 * [https://github.com/tianzhijiexian/CommonAdapter]() 

在此特别感谢上述作者，同时欢迎大家下载体验本项目，如果使用过程中遇到什么问题，欢迎反馈。
## 联系方式
 * QQ： 382060748 
 * QQ群： 238972269  
 * 本群旨在为使用我的github项目的人提供方便，如果遇到问题欢迎在群里提问。个人能力也有限，希望一起学习一起进步。

## 能提供一下帮助
*  支持1种或者多种类型的item
*  提升item的独立性，相同的Item整个项目只需写一个类
*  一个item仅会调用一次setViews()，避免重复建立监听器
*  可以给RecyclerView添加头、
*  可以给RecyclerView自定义加载更多
*  可以设置空布局
*  提供了getConvertedData(data, type)方法来对item传入的数据做转换，方便拆包和提升item的复用性



#1、设置布局文件

    
    <com.luoxiong.view.RecyclerViewParent
        android:id="@+id/id_RecyclerViewParent"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
  
#2、使用加载更多必看
##如果列表有加载更多，服务器一般会以分页的形式返回数据，我们怎么才知道是否是最后一页呢？服务器端大概有2种告诉我们的方式。
###1、告诉你Item总数,比如总共有100条数据，当我们每次以20条加载，加载完所有数据后，加载更多隐藏，不在触发加载更多

###2、我们每次请求20条数据，假如请求第4次的时候服务器返回19条，此时说明服务器没有数据了，不在触发加载更多
#### 现在这些通通不是问题，只需一行代码搞定
				
		    //设置Item总数
         mAdapter1.setItemTotal(100);
		
		//只有调用这个方法才会开启加载更多
		mAdapter1.setOnLoadMoreListener(){
		@Override
            public void onLoadMore() {
            
                 List<ModelData> datas = DataManager.loadModelData(5);
				 //服务器以第1种方式告诉我们Item总数量
				 //第2个参数： true是表示加载更多，false表示下拉刷新 
				 //第3个参数：Item总数，和上面那个方法的作用一样，如果没有调用setItemTotal，这里应该传具体的Item总数，否则传0或者-1即可。
                 mAdapter1.addDatas(datas, true, 0);
               	
				//服务器以第2种方式告诉我们是否有加载更多
				//第1个参数： pageSize的意思，如果datas.size只有19条，默认就不再触发加载更多逻辑
				mAdapter1.addDatas(20, datas，true);
            }



##3、具体使用步骤
 
		
        mRecyclerViewParent = (RecyclerViewParent) view.findViewById(R.id.id_RecyclerViewParent);
        mRecyclerViewParent.setLinearLayoutManager(true);
  		//禁止下拉刷新
        mRecyclerViewParent.setRefreshEnable(false);
        mAdapter1 = new MyAdapter1(getContext(), mDatas, mRecyclerViewParent);
		//添加头
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.head_layout, mRecyclerViewParent, false);
        mAdapter1.setHeadView(headView);
		
		//添加空布局
		 View vvv = LayoutInflater.from(getActivity()).inflate(R.layout.empty_layout, mRecyclerViewParent, false);
        mAdapter1.setEmptyView(vvv);
		//设置适配器
        mRecyclerViewParent.setAdapter(mAdapter1);
		//点击 和 下拉刷新 监听
        mRecyclerViewParent.setOnRecyclerViewListener(new RecyclerViewParent.OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                 
            }

            @Override
            public void onItemRefresh() {

            }
        });

		//也可以自定义加载更多的布局
		  mAdapter1.setLoadMoreView(...
		)
					
			class MyAdapter1 extends BaseRecyAP<TextBean> {
		        public MyAdapter1(Context context, List<TextBean> datas,
		                          RecyclerViewParent recyParent) {
		            super(context, datas, recyParent);
		        }
		
		        @Override
		        public IAdapterHolder createItem(Object type) {
		            return new TextBeanHolder();
		        }
		    }
		
```

##3、上面的TextBeanHolder是全局复用的。更多方法请看Demo

### 设计思路

**1. Adapter**  

如果用adapter常规写法，你会发现代码量很大，可读性低。如果adapter中有多个类型的Item，我们还得在getView()中写很多if-else语句，很乱。
而现在我让adapter的代码量减少到一个8行的内部类，如果你需要更换item只需要动一行代码，真正实现了可插拔化。最关键的是item现在作为了一个独立的对象，可以方便的进行复用。

**2. AdapterHolder**  

和原来方式最为不同的一点就是我把adapter的item作为了一个实体，这种方式借鉴了RecyclerView中ViewHolder的设计。把item作为实体的好处有很多，比如复用啊，封装啊，其余的就不细说了。  

**3. 分层**  

在使用过程中，我发现如果adapter放在view层，那就会影响到view层的独立性。此外adapter中经常有很多数据处理的操作，比如通过type选择item，数据的拆包、转换等操作。于是我还是推荐把adapter放在mvp的p层，或者是mvvm的m层。通过在实际的项目中使用来看，放在m或p层的效果较好，view的复用也比较好做。


## 开发者

![](https://avatars0.githubusercontent.com/u/16216657?v=3&u=6bd35ed04bd234c24938bc0986e7f6ef4e525ed0&s=400)

我的邮箱: <382060748@qq.com>  


## License

```  
Copyright 2016-2019 Jack Tony

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
