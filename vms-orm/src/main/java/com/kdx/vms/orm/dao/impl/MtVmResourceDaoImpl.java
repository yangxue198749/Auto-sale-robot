package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtVmResource;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.MtVmResourceDao;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zhangguodong on 2017/5/23.
 */
@Service("mtVmResourceDao")
public class MtVmResourceDaoImpl implements MtVmResourceDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<TMtVmResource> findPage(TSysUser user, String name, Integer pageSize,Integer type) {
        try {

            Expression qualifier = ExpressionFactory.expTrue();
            if(!StringUtil.isEmpty(name)){
                qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtVmResource.RESOURCE_NAME_PROPERTY,"%"+name.trim()+"%"));
            }
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVmResource.RESOURCE_TYPE_PROPERTY,type));
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVmResource.DELETE_MARK_PROPERTY,0));
            SelectQuery select = new SelectQuery(TMtVmResource.class, qualifier);
            if(null != pageSize){
                select.setPageSize(pageSize);
            }
            return user.getObjectContext().performQuery(select);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return null;
    }

    @Override
    public TMtVmResource findById(TSysUser user, Integer id) {
        return Cayenne.objectForPK(user.getObjectContext(),TMtVmResource.class,id);
    }


}
