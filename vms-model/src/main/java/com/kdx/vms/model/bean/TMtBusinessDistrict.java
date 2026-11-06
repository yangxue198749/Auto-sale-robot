package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TMtBusinessDistrict;
import org.apache.cayenne.Cayenne;

public class TMtBusinessDistrict extends _TMtBusinessDistrict {

	public Integer getId(){
		return Cayenne.intPKForObject(this);
	}

}
