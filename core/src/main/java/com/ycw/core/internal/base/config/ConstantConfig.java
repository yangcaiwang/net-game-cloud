//
// 由游戏编辑器自动创建修改,请勿修改
//

package com.ycw.core.internal.base.config;

public class ConstantConfig extends AbstractConfig {
    /// <summary>  id  </summary>
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /// <summary>  参数  </summary>
    private int[] constant;

    public int[] getConstant() {
        return constant;
    }

    public void setConstant(int[] constant) {
        this.constant = constant;
    }

    public int getParamValue(int index){
        if (index < 0) return 0;
        int[] constant = getConstant();
        if(constant == null || constant.length <= index) return 0;
        return constant[index];
    }
}