package com.asiainfo.selforder.model.dishes;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *         2015年6月30日
 * 
 *         菜品种类实体
 */
public class MerchantDishesType extends DataSupport implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**菜品名称**/
	private String dishesTypeName;  
	/**菜品编码**/
	private String dishesTypeCode;
	private String startDate;
	private String endDate;
	private String merchantId;
	private String dishesNum;
    private List<MerchantDishes> dishesInfoList=new ArrayList<MerchantDishes>();
    private int sectionPosition;

    public int getSectionPosition() {
        return sectionPosition;
    }

    public void setSectionPosition(int sectionPosition) {
        this.sectionPosition = sectionPosition;
    }

    public String getDishesTypeName() {
		return dishesTypeName;
	}

	public void setDishesTypeName(String dishesTypeName) {
		this.dishesTypeName = dishesTypeName;
	}

	public String getDishesTypeCode() {
		return dishesTypeCode;
	}

	public void setDishesTypeCode(String dishesTypeCode) {
		this.dishesTypeCode = dishesTypeCode;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getDishesNum() {
		return dishesNum;
	}

	public void setDishesNum(String dishesNum) {
		this.dishesNum = dishesNum;
	}

	public List<MerchantDishes> getDishesInfoList() {
		return dishesInfoList;
	}

	public void setDishesInfoList(List<MerchantDishes> dishesInfoList) {
		this.dishesInfoList = dishesInfoList;
	}

    public int getId(){
        return Integer.valueOf(getBaseObjId() + "");
    }

    /**
     * 默认懒加载，查询出关联表中的数据，来实现表的关联表的继续迭代查询关联表数据
     * 连缀查询，我们就可以将关联表数据的查询延迟
     * 这种写法会比激进查询更加高效也更加合理
     *
     * @return
     */
    public List<MerchantDishes> getItemlistDb() {
        this.dishesInfoList = (ArrayList<MerchantDishes>) DataSupport.where("merchantdishestype_id = ?", String.valueOf(getId())).find(MerchantDishes.class);
        return dishesInfoList;
    }
}
