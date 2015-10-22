package com.special.animatedrandomlayout.random_layout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.special.animatedrandomlayout.activity.LogUtil;

/**
 * RandomLayout，其主要目的是为了按要求实现随机布局。
 * @Using-Step
 * 				 setRegularity </br>
 * 				 -> setItemShowCount </br>
 * 				 -> setLooperDuration </br>
 * 				 -> setDefaultDruation </br>
 * 				 -> setOnCreateItemViewListener( getCount ; createItemView )
 * @attention
 * 使用此随机布局，必须重写{@link OnCreateItemViewListener}回调接口的{@link createItemView}
 * 方法，来定义用于随机布局的子控件
 * @author Windsander
 *
 */
@SuppressLint("HandlerLeak")
public class AnimatedRandomLayout extends FrameLayout {
	
//参数声明/**************************************************************************************/
	/** 用于生成随机偏移量的Random对象 */
	private Random mRandom;
	
	//矩阵 与 布局参数计算相关变量===================================================
	/** 区域的二维数组，即密度矩阵 */
	private int[][] mAreaDensity;
	/** X分布规则性，该值越高，子view在x方向的分布越规则、平均。最小值为1。 */
	private int mXRegularity;
	/** Y分布规则性，该值越高，子view在y方向的分布越规则、平均。最小值为1。 */
	private int mYRegularity;
	/** 记录当前密度下，用于放置View的区块数量 */
	private int mAreaNum;
	
	//当前布局子控件的动画控制参数===================================================
	/** 记录需要开启动画的新加入的子控件 */
	private List<View> justInitChilds;
	/** 记录当前布局中心位置 */
	private Point mCenter;
	/** 记录当前布局对角线半径 */
	private float mDiagonalLength;
	/** 记录动画最长持续时间 ，默认为 2000*/
	private int mDefaultDruation = 2000;
	/** 记录子控件自动生成时间间隔，默认为 1000 */
	private int mLooperDuration = 1000;
	
	//当前布局子控件的细节控制参数===================================================
	/** 同一时刻，被展示到控件上的 子View 个数最大值 */
	private int mItemShowCount = 1;
	/** 计算重叠时候的子控件安全间距 */
	private int mOverlapAdd = 2;
	/** 存放打算让云布局显示的子控件总数 */
	private int mTotalViewNum;
	
	//当前显示记录器  与  缓存复用记录器================================================
	/** 用于存放以分配了位置显示的 View，仅仅用于检测是否显示 */
	private List<View> mFixedViews;
	/** 存放可用区块ID */
	private List<Integer> availAreas;
	/** 用于存放被回收了的View，便于复用 */
	private List<View> mRecycledViews;
	/** 布局完成状态记录 */
	private boolean mIsLayout = false;
	
	//Handler循环生成当前时刻子控件================================================
	private Handler handler;
	{
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				loopChild();
			}
		};
	}

	
//构造方法/**************************************************************************************/
	public AnimatedRandomLayout(Context context) {
		this(context, null);
	}

	public AnimatedRandomLayout(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}
	
	public AnimatedRandomLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
//初始化方法/*************************************************************************************/
	private void init() {
		mRandom = new Random();
		setRegularity(1, 1);      //避免  NullPointerException
		
		mFixedViews = new ArrayList<View>();
		mRecycledViews = new ArrayList<View>();
		
		availAreas = new ArrayList<Integer>(mAreaNum);
		resetAvailAreas();
		
		mCenter = new Point();
		
	}

//测量与构建/*************************************************************************************/
	/**
	 * 用于开启循环展示
	 */
	public void start(){
		removeAllViews();
		mTotalViewNum = onCreateItemViewListener.getCount();
		justInitChilds = new ArrayList<View>();
		//定义子控件出现的时间间隔
		loopChild();
	}

	private void loopChild() {
		//初始化布局界面
		resetPanelForChild();
		//生成子控件
		generateChild();
		//在生成孩子布局完成后，开始动画
		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				getViewTreeObserver().removeGlobalOnLayoutListener(this);
				
				startZoomAnimation();
			}
		});
		handler.sendEmptyMessageDelayed(0, mLooperDuration);
	}
	
	/**
	 * 根据设定生成 展示用子View，根据设定的同时可出现的子控件上限，来动态生成子控件
	 */
	private void generateChild(){
		if(onCreateItemViewListener == null){
			return;
		}
		//fixedViewCount用于存放已经显示在当前布局的View的个数 
		int fixedViewCount = mFixedViews.size();
		int count = fixedViewCount + mRandom.nextInt(mItemShowCount);
		LogUtil.LOGW("tag", "count:"+count); //TODO
		for (int i = count-1; i >= fixedViewCount; i--) {
			View convertView = popRecycler();
			View newChild = onCreateItemViewListener.createItemView(i % mTotalViewNum, convertView);
			//判断是否发生复用，如果没发生，则存入当前View
			if(newChild != convertView){
				pushRecycler(convertView);
			}
			ChildViewBound params = new ChildViewBound(
					LayoutParams.WRAP_CONTENT, 
					LayoutParams.WRAP_CONTENT);
			newChild.setLayoutParams(params);
			//因为我们使用的是 TextView 作为子控件，因此，使用其自带的测量方法即可
			addView(newChild);
			justInitChilds.add(newChild);
		}
		
	}
	
/*	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//测量每个子控件，以便于后续使用
		int childNum = this.getChildCount();
		for (int i = 0; i < childNum; i++) {
			int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
			int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST);
			this.getChildAt(i).measure(childWidthMeasureSpec, childHeightMeasureSpec);
		}
	}*/
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childNum = this.getChildCount();
		//确定可供显示的区域大小
		int thisW = r - l - this.getPaddingLeft() - this.getPaddingRight();
		int thisH = b - t - this.getPaddingTop() - this.getPaddingBottom();
		//计算当前布局中心
		mCenter.x = thisW / 2;
		mCenter.y = thisH / 2;
		//计算当前布局对角线长度
		mDiagonalLength = GeometryUtil.getDistanceBetween2Points(
				l - this.getPaddingLeft(), b - this.getPaddingBottom(),
				mCenter);
		//获取每个区块的宽高
		float blockW = thisW / (float) mXRegularity;
		float blockH = thisH / (float) mYRegularity;
		
		//重置可用控件列表
		resetAvailAreas();
		
		//计算区块容积，容积至少唯一，当区块数小于子控件数时，容积 > 1
		int blockCapacity = ((childNum + 1) / mAreaNum) + 1 ;
		
		int availAreaNum = mAreaNum;
		for (int i = 0; i < childNum; i++) {
			//获取子控件，并检测显示情况，当为GONE时，则不用安排布局
			View child = this.getChildAt(i);
			child.measure(0, 0);
			
			if(child.getVisibility() == View.GONE){
				continue;
			}
			//检测子控件是否已经分配了位置，没有时才进行位置分配
			if(!mFixedViews.contains(child)){
				int childW = child.getMeasuredWidth();
				int childH = child.getMeasuredHeight();
				// 求得子控件左上角的取值上限
				int leftEdge = r - getPaddingRight() - childW;
				int topEdge = b - getPaddingBottom() - childH;
				
				//位置分配：直到确实没有空间可供使用之前，随机寻找子控件存放位置
				while(availAreaNum > 0){
					//计算随机块，用于存放当前View
					int availId = mRandom.nextInt(availAreaNum);  //从可用区块列表中，获取随机值对应的区块编号
					int childPositionId = availAreas.get(availId);
					int pRow = childPositionId / mXRegularity;
					int pCol = childPositionId % mXRegularity;
					
					//为了保证每个区块充分使用，进行容量判断
					if(mAreaDensity[pCol][pRow] < blockCapacity){
						//计算区块空余
						int xOffset = childInBlockOffestX((int) blockW, childW);
						int yOffset = childInBlockOffsetY((int) blockH, childH);
						
						//这里的 LayoutParams 仅仅为自定义，仅仅是为了在满足要求下，能对应保存参数数据
						ChildViewBound newChildBound = (ChildViewBound) child.getLayoutParams();
						int childLeft = (int) (pCol * blockW) + this.getPaddingLeft() + xOffset;
							childLeft = Math.min(childLeft, leftEdge);
						int childTop = (int) (pRow * blockH) + this.getPaddingTop() + yOffset;
							childTop = Math.min(childTop, topEdge);
						int childRight = childLeft + childW;
						int childBottom = childTop + childH;
						newChildBound.setChildViewBound(
								childLeft, childTop, childRight, childBottom);

						//判断是否发生重叠，如果没有发生重叠，则布局并记录，否则重新计算位置
//						if(!isOverLap(newChildBound)){
							child.setLayoutParams(newChildBound);
							child.layout(childLeft, childTop, childRight, childBottom);
							LogUtil.LOGW("tag", "layout!!!!!!!!!!!!!" + childLeft +
									     "       availAreaNum:" + availAreaNum);//TODO
							mFixedViews.add(child);
							mAreaDensity[pCol][pRow]++;
							//已完成当前View的布局，跳出随机布局循环
							break;
//						}
//						else{
//							availAreas.remove((Integer)childPositionId);
//							availAreaNum--;
//						}
					}else{
						availAreas.remove((Integer)childPositionId);
						availAreaNum--;
					}
				}
			}
		}
		//已经完成布局
		mIsLayout = true;
	}
	
	/**
	 * 判断当前 View 布局位置是否与已经显示的View有重叠
	 * @param newChildBound 需要被检测的 View
	 * @return true：表示重叠；  false：表示不重叠
	 */
	@SuppressWarnings("unused")
	private boolean isOverLap(ChildViewBound newChildBound){
		for (View preChild : mFixedViews) {
			//计算重叠空间
			ChildViewBound preChildBound = (ChildViewBound) preChild.getLayoutParams();
		
			int left = Math.max(newChildBound.getChildLeft() - mOverlapAdd,
					preChildBound.getChildLeft() - mOverlapAdd);
			int top = Math.max(newChildBound.getChildTop() - mOverlapAdd,
					preChildBound.getChildTop() - mOverlapAdd);
			int right = Math.min(newChildBound.getChildRight() + mOverlapAdd,
					preChildBound.getChildRight() + mOverlapAdd);
			int bottom = Math.min(newChildBound.getChildBottom() + mOverlapAdd,
					preChildBound.getChildBottom() + mOverlapAdd);
			
			if((right - left) > 0 || (bottom - top) > 0){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 使得自动生成子控件并填装步骤，在用户切出界面后，不再执行，避免占用CPU资源
	 */
	@Override
	protected void onDetachedFromWindow() {
		handler.removeCallbacksAndMessages(null);
		super.onDetachedFromWindow();
	}

	
//工具包方法/**************************************************************************************/
	//设置初始属性=============================================================
	/**
	 * 设定当前密度矩阵行列数，同时计算相关 密度矩阵，及总区块数
	 * @param xRegularity 设定密度矩阵行数
	 * @param yRegularity 设定密度矩阵列数
	 */
	public void setRegularity(int xRegularity, int yRegularity){
		this.mXRegularity = (xRegularity > 1) ? xRegularity : 1;
		this.mYRegularity = (yRegularity > 1) ? yRegularity : 1;
		//按设置，计算区块总数
		this.mAreaNum = mXRegularity * mYRegularity;
		initAreaDensity();
		
	}

	/**
	 * 按设置，初始化密度矩阵
	 */
	private void initAreaDensity() {
		this.mAreaDensity = new int[mXRegularity][mYRegularity];
		resetAreasDensity();
	}
	
	/**
	 * 设置同一时刻，被展示到控件上的 子View 个数最大值，默认为 1
	 * @param itemShowCount 个数最大值
	 */
	public void setItemShowCount(int itemShowCount){
		this.mItemShowCount = (itemShowCount > 1) ?itemShowCount : 1;
	}
	
	/**
	 * 设置子控件自动生成时间间隔
	 */
	public void setLooperDuration(int mLooperDuration) {
		this.mLooperDuration = mLooperDuration;
	}

	/**
	 * 设置动画最长持续时间
	 */
	public void setDefaultDruation(int mDefaultDruation) {
		this.mDefaultDruation = mDefaultDruation;
	}

	//计算关键差值=============================================================
	/**
	 * 计算区块和子控件宽度大小之间的大小差值
	 * @param blockW 区块宽度
	 * @param childWidth 子控件宽度
	 * @return 宽度差值
	 */
	private int childInBlockOffestX(int blockW, int childWidth){
		int xOffset = blockW - childWidth;
		if(xOffset <= 0){
			xOffset = 1;
		}
		return mRandom.nextInt(xOffset);
	}
	
	/**
	 * 计算区块和子控件高度大小之间的大小差值
	 * @param blockH 区块高度
	 * @param childHeight 子控件高度
	 * @return 高度差值
	 */
	private int childInBlockOffsetY(int blockH, int childHeight){
		int yOffset = blockH - childHeight;
		if(yOffset <= 0){
			yOffset = 1;
		}
		return mRandom.nextInt(yOffset);
	}
	
	//重置关键参数=============================================================
	/**
	 * 初始化可用区块
	 */
	private void resetAvailAreas() {
		availAreas.clear();
		for (int i = 0; i < mAreaNum; i++) {
			availAreas.add(i);
		}
	}
	
	/**
	 * 重置密度矩阵 {@link mAreaDensity}
	 */
	private void resetAreasDensity(){
		if(mAreaDensity != null){
			for (int i = 0; i < mXRegularity; i++) {
				for (int j = 0; j < mYRegularity; j++) {
					mAreaDensity[i][j] = 0;
				}
			}
		}
	}
	
	/**
	 * 清空复用缓存列表
	 */
	private void resetRecycler(){
		if(mRecycledViews != null){
			mRecycledViews.clear();
		}
	}
	
	/**
	 * 生成并布局子控件之前，先初始化布局环境记录
	 */
	private void resetPanelForChild(){
		resetAreasDensity();
		resetRecycler();
	}
	
	//复用缓存列表操作===========================================================
	/**
	 * 把复用的View加入复用列表栈顶，FILO
	 * @param scrapView 要添加入复用列表的View
	 */
	private void pushRecycler(View scrapView){
		if (null != scrapView) {
			mRecycledViews.add(0, scrapView);
		}
	}
	
	/**
	 * 取出缓存复用列表保有的 View，FILO
	 * @return 栈顶 View
	 */
	private View popRecycler(){
		final int size = mRecycledViews.size();
		if(size > 0){
			return mRecycledViews.remove(0);
		}else{
			return null;
		}
	}
	
	//开启 子控件  动画===========================================================
	private void startZoomAnimation(){
		for (final View justInitChild : justInitChilds) {
			ChildViewBound params = (ChildViewBound) justInitChild.getLayoutParams();
			//计算控件动画动态配置参数，用于设置动画持续时间
			float distance = GeometryUtil.getDistanceBetween2Points(params, mCenter);
			float percent = distance / mDiagonalLength;
			//动态设置子控件动画实际持续时间，越靠近中心，消失的越快
			int duration = (int) (mDefaultDruation * percent + 0.5f);
			//计算控件移动方向与距离
			float dx = GeometryUtil.caculateDx(params, mCenter) ;
			float dy = GeometryUtil.caculateDy(params, mCenter) ;
			LogUtil.LOGW("tag", "x:" + mCenter.x + "   y:" + mCenter.y); //TODO
			
			AnimatorUtil animatorUtils = new AnimatorUtil(justInitChild, duration);
			animatorUtils.addAlphaAnimationBy(-1.0f)
						 .addTranslationAnimationBy(dx, dy)
						 .addScaleAnimationBy(-0.8f)
						 .startAnimator();
			ViewPropertyAnimator animate = animatorUtils.getAnimate();
			animate.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					justInitChild.clearAnimation();
					mFixedViews.remove(justInitChild);
					pushRecycler(justInitChild);
					removeView(justInitChild);
				}
			});
		}
		justInitChilds.clear();
		
	}
	
	//阶段完成情况获取===========================================================
	/**
	 * 返回当前 RandomLayout 是否已经执行 onLayout 
	 * @return 当前布局状态标识
	 */
	public boolean isLayout(){
		return mIsLayout;
	}
	
	//对外暴露方法=============================================================
	/** 
	 * 重写父类的removeAllViews 
	 */
	@Override
	public void removeAllViews() {
		super.removeAllViews();//先删除所有View
		resetAreasDensity();//重新设置所有区域的区域密度
		resetRecycler();//清空缓存列表
	}
	
	/**
	 * 请求刷新当前View显示，会重新分配布局
	 */
	public void refreshView(){
		resetAreasDensity();
		requestLayout();
	}
	
	public int getLooperDuration() {
		return mLooperDuration;
	}
	
	public int getDefaultDruation() {
		return mDefaultDruation;
	}
	
//回调函数定义/*************************************************************************************/
	private OnCreateItemViewListener onCreateItemViewListener;

	/**
	 * 用于监听布局生成用于显示的子控件，使用布局，必须重写该监听的 {@link createItemView} 方法
	 */
	public static interface OnCreateItemViewListener{
		public int getCount();  //设置用于显示的子控件数目
		public View createItemView(int position, View convertView); //用于获取指定位置的子控件
	}
	
	public void setOnCreateItemViewListener(OnCreateItemViewListener createItemViewListener){
		this.onCreateItemViewListener = createItemViewListener;
		
	}
	
}


