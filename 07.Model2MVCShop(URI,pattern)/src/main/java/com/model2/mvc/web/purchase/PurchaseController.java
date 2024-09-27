 package com.model2.mvc.web.purchase;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.user.UserService;


//==> 회원관리 Controller
@Controller
public class PurchaseController {
	
	///Field
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;
	
	//setter Method 구현 않음
		
	public PurchaseController(){
		System.out.println(this.getClass());
	}
	
	//==> classpath:config/common.properties  ,  classpath:config/commonservice.xml 참조 할것
	//==> 아래의 두개를 주석을 풀어 의미를 확인 할것
	@Value("#{commonProperties['pageUnit']}")
	//@Value("#{commonProperties['pageUnit'] ?: 3}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	//@Value("#{commonProperties['pageSize'] ?: 2}")
	int pageSize;
	
	@RequestMapping("/addPurchaseView.do")
	public ModelAndView addPurchaseView(@RequestParam("prodNo") int prodNo , HttpSession session) throws Exception {
		System.out.println("/addPurchaseView.do");
		
		User user = (User)session.getAttribute("user");
		if(user == null) {
			return new ModelAndView("/listProduct.do?menu=search");
		}
		
		Product product = productService.getProduct(prodNo);
		System.out.println(product);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("purchase/addPurchaseView.jsp");
		modelAndView.addObject("user",user);
		modelAndView.addObject("product", product);
				
		return modelAndView;
	}
	
	
	@RequestMapping("/addPurchase.do")
	public ModelAndView addPurchase( @ModelAttribute("purchase")Purchase purchase ) throws Exception {
		System.out.println("/addPurchase.do");
		
		User user = userService.getUser(purchase.getBuyerId());
		Product product = productService.getProduct(purchase.getProdNo());
		
		purchase.setBuyer(user); 
		purchase.setPurchaseProd(product);
		
		System.out.println(purchase);
		//Business Logic
		purchaseService.addPurchase(purchase);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("purchase/addPurchase.jsp");
		modelAndView.addObject("purchase",purchase);
		
		
		return modelAndView;
	}
	
	@RequestMapping("/getPurchase.do")
	public ModelAndView getPurchase( @RequestParam("tranNo") int tranNo) throws Exception {
		
		System.out.println("/getProduct.do");
		//Business Logic
		Purchase purchase = purchaseService.getPurchase(tranNo);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("purchase/getPurchase.jsp");
		modelAndView.addObject("purchase", purchase);
		

		return modelAndView;

	}
	
	@RequestMapping("/updatePurchaseView.do")
	public ModelAndView updatePurchaseView( @RequestParam("tranNo") int tranNo ) throws Exception{

		System.out.println("/updateProductView.do?tranNo="+tranNo);
		//Business Logic
		Purchase purchase = purchaseService.getPurchase(tranNo);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("product/updatePurchaseView.jsp");
		modelAndView.addObject("puchase", purchase);
		
		return modelAndView;
	}
	
	@RequestMapping("/updatePurchase.do")
	public ModelAndView updatePurchase( @ModelAttribute("purchase") Purchase purchase) throws Exception{

		System.out.println("/updateProduct.do?tranNo="+purchase.getTranNo());
		//Business Logic
		purchaseService.updatePurchase(purchase);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/getPurchase.do?tranNo=" + purchase.getTranNo());
		
		return modelAndView;
	}
	
	@RequestMapping("/updateTranCode.do")
	public ModelAndView updateTranCode( @RequestParam("tranNo") int tranNo, @RequestParam("tranCode") int tranCode, @RequestParam("currentPage") int currentPage) throws Exception{

		System.out.println("/updateTranCode.do?prodNo="+tranNo+"&tranCode="+tranCode+"&currentPage="+currentPage);
		//Business Logic
		Search search = new Search();
		search.setCurrentPage(currentPage);
		
		Purchase purchase = purchaseService.getPurchase(tranNo);
		System.out.println(purchase);
		
		purchaseService.updateTranCode(purchase, tranCode);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/listPurchase.do");
		modelAndView.addObject("search",search);
		
		return modelAndView;
	}
	
	@RequestMapping("/updateTranCodeByProd.do")
	public ModelAndView updateTranCodeByProd( @RequestParam("prodNo") int prodNo, @RequestParam("tranCode") int tranCode, @RequestParam("currentPage") int currentPage) throws Exception{

		System.out.println("/updateTranCodeByProd.do?prodNo="+prodNo+"&tranCode="+tranCode+"&currentPage="+currentPage);
		//Business Logic
		Search search = new Search();
		search.setCurrentPage(currentPage);
		
		Purchase purchase = purchaseService.getPurchaseByProd(prodNo);
		System.out.println(purchase);
		
		purchaseService.updateTranCode(purchase, tranCode);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/listProduct.do?menu=manage");
		modelAndView.addObject("search",search);
		
		return modelAndView;
	}
	
	@RequestMapping("/listPurchase.do")
	public ModelAndView listPurchase(@ModelAttribute("search") Search search, HttpSession session) throws Exception{
		
		System.out.println("/listPurchase.do");
		User user = (User)session.getAttribute("user");
		if(user == null) {
			return new ModelAndView("redirect:/login.do");
		}
		
		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		// Business logic 수행
		Map<String , Object> map=purchaseService.getPurchaseList(search, user.getUserId());
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		
		// Model 과 View 연결
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("purchase/listPurchase.jsp");
		modelAndView.addObject("list",map.get("list"));
		modelAndView.addObject("resultPage", resultPage);
		modelAndView.addObject("search", search);
		
		return modelAndView;
	}
}
