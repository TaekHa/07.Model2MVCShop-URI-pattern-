package com.model2.mvc.service.purchase.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductDao;
import com.model2.mvc.service.purchase.PurchaseDao;
import com.model2.mvc.service.user.UserDao;

@Repository("purchaseDaoImpl")
public class PurchaseDaoImpl implements PurchaseDao{
	
	
	//Field
	@Autowired
	@Qualifier("sqlSessionTemplate")
	private SqlSession sqlSession;
	UserDao userDAO;
	ProductDao productDAO;

	public PurchaseDaoImpl() {
		// TODO Auto-generated constructor stub
		System.out.println(this.getClass());
	}
	
	//method
	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}
	
	public void addPurchase(Purchase purchase) throws Exception{
		sqlSession.insert("PurchaseMapper.addPurchase",purchase);
	}
	
	public Purchase getPurchase(int tranNo) throws Exception {
		return sqlSession.selectOne("PurchaseMapper.getPurchase", tranNo);
	}
	
	public Purchase getPurchaseByProd(int prodNo) throws Exception {
		
		return sqlSession.selectOne("PurchaseMapper.getPurchaseByProd", prodNo);
		
	}
	
	//work from here!
	public List<Purchase> getPurchaseList(Map<String, Object> map ) throws Exception{
				
		return sqlSession.selectList("PurchaseMapper.getPurchaseList", map);
	}
		
	
	public Map<String, Object> getSaleList(Search search) throws Exception{
		
		return null;
	}
	
	
	public void updatePurchase(Purchase purchase) throws Exception{
			
		sqlSession.update("PurchaseMapper.updatePurchase",purchase);		
	}
	
	public void updateTranCode(Purchase purchase, int tranStatusCode) throws Exception{
		
		Map<String, Object> purchaseMap = new HashMap<String, Object>();
		purchaseMap.put("purchase", purchase);
		purchaseMap.put("tranStatusCode", tranStatusCode);
		
		sqlSession.update("PurchaseMapper.updateTranCode",purchaseMap);				
	}
	
	// 게시판 Page 처리를 위한 전체 Row(totalCount)  return
	public int getTotalCount(String buyerId) throws Exception {
		
		return sqlSession.selectOne("PurchaseMapper.getTotalCount", buyerId);
	}

}
