package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TMtCompany;
import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import java.util.ArrayList;
import java.util.List;

public class TMtCompany extends _TMtCompany {
	
	/** 一级公司 */
	public final static int LEVEL_ONE   = 1;
	/** 二级公司 */
	public final static int LEVEL_TWO   = 2;
	/** 三级公司 */
	public final static int LEVEL_THREE = 3;

	private TSysUser adminUser;

    public Integer getId() {
        return Cayenne.intPKForObject(this);
    }

	public TSysUser getAdminUser() {
		return adminUser;
	}

	public void setAdminUser(TSysUser adminUser) {
		this.adminUser = adminUser;
	}

	public List<TMtCompany> getAllChildCompany() {
		switch (getCompanyLevel()){
			case LEVEL_ONE:
				Expression qualifier= ExpressionFactory.matchExp(COMPANY_LEVEL_PROPERTY,LEVEL_THREE);
				SelectQuery select = new SelectQuery(TMtCompany.class, qualifier);
				return objectContext.performQuery(select);
			case LEVEL_TWO:
				return this.getChildCompany();
			case LEVEL_THREE:
				List<TMtCompany> list=new ArrayList<>();
				list.add(this);
				return list;
			default:
				return new ArrayList<>();
		}
	}

    @Override
    public List<TSysUser> getUser() {
        List<TSysUser> list = super.getUser();
        if (null == list) return null;
        Expression expression = ExpressionFactory.matchExp(TSysUser.DELETE_MARK_PROPERTY, TSysUser.DELETE_MARK_EXIST);
        return expression.filterObjects(list);
    }

    public static void main(String[] args) {
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TMtCompany company = Cayenne.objectForPK(context, TMtCompany.class, 520);
        System.out.println(company.getUser());
    }

}
