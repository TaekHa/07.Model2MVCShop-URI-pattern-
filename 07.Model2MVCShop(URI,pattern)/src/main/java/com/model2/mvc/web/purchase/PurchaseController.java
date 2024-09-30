 package com.model2.mvc.web.purchase;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
@RequestMapping("/purchase/*")
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
	
	@RequestMapping(value="addPurchase", method=RequestMethod.GET)
	public ModelAndView addPurchaseView(@RequestParam("prodNo") int prodNo , HttpSession session) throws Exception {
		System.out.println("/purchase/addPurchase : GET");
		
		User user = (User)session.getAttribute("user");
		if(user == null) {
			return new ModelAndView("/product/listProduct?menu=search");
		}
		
		Product product = productService.getProduct(prodNo);
		System.out.println(product);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/addPurchaseView.jsp");
		modelAndView.addObject("user",user);
		modelAndView.addObject("product", product);
				
		return modelAndView;
	}
	
	@RequestMapping(value="addPurchase", method=RequestMethod.POST)
	public ModelAndView addPurchase( @ModelAttribute("purchase")Purchase purchase ) throws Exception {
		System.out.println("/purchase/addPurchase : POST");
		
		User user = userService.getUser(purchase.getBuyerId());
		Product product = productService.getProduct(purchase.getProdNo());
		
		purchase.setBuyer(user); 
		purchase.setPurchaseProd(product);
		
		System.out.println(purchase);
		//Business Logic
		purchaseService.addPurchase(purchase);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/addPurchase.jsp");
		modelAndView.addObject("purchase",purchase);
		
		
		return modelAndView;
	}
	
	@RequestMapping(value = "getPurchase", method=RequestMethod.GET)
	public ModelAndView getPurchase( @RequestParam("tranNo") int tranNo) throws Exception {
		
		System.out.println("/purchase/getProduct : GET");
		//Business Logic
		Purchase purchase = purchaseService.getPurchase(tranNo);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/getPurchase.jsp");
		modelAndView.addObject("purchase", purchase);
		

		return modelAndView;

	}
	
	@RequestMapping(value="updatePurchase", method=RequestMethod.GET)
	public ModelAndView updatePurchaseView( @RequestParam("tranNo") int tranNo ) throws Exception{

		System.out.println("/purchase/updatePurchase?tranNo="+tranNo+" : GET");
		//Business Logic
		Purchase purchase = purchaseService.getPurchase(tranNo);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/updatePurchaseView.jsp");
		modelAndView.addObject("puchase", purchase);
		
		return modelAndView;
	}
	
	@RequestMapping(value="updatePurchase", method=RequestMethod.POST)
	public ModelAndView updatePurchase( @ModelAttribute("purchase") Purchase purchase) throws Exception{

		System.out.println("/purchase/updatePurchase?tranNo="+purchase.getTranNo()+" : POST");
		//Business Logic
		purchaseService.updatePurchase(purchase);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/purchase/getPurchase?tranNo=" + purchase.getTranNo());
		
		return modelAndView;
	}
	
	@RequestMapping(value="updateTranCode")
	public ModelAndView updateTranCode( @RequestParam("tranNo") int tranNo, @RequestParam("tranCode") int tranCode, @RequestParam("currentPage") int currentPage) throws Exception{

		System.out.println("/updateTranCode?prodNo="+tranNo+"&tranCode="+tranCode+"&currentPage="+currentPage + " : GET/POST");
		//Business Logic
		Search search = new Search();
		search.setCurrentPage(currentPage);
		
		Purchase purchase = purchaseService.getPurchase(tranNo);
		System.out.println(purchase);
		
		purchaseService.updateTranCode(purchase, tranCode);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/listPurchase");
		modelAndView.addObject("search",search);
		
		return modelAndView;
	}
	
	@RequestMapping(value = "updateTranCodeByProd")
	public ModelAndView updateTranCodeByProd( @RequestParam("prodNo") int prodNo, @RequestParam("tranCode") int tranCode, @RequestParam("currentPage") int currentPage) throws Exception{

		System.out.println("/updateTranCodeByProd?prodNo="+prodNo+"&tranCode="+tranCode+"&currentPage="+currentPage + " : GET/POST");
		//Business Logic
		Search search = new Search();
		search.setCurrentPage(currentPage);
		
		Purchase purchase = purchaseService.getPurchaseByProd(prodNo);
		System.out.println(purchase);
		
		purchaseService.updateTranCode(purchase, tranCode);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/product/listProduct?menu=manage");
		modelAndView.addObject("search",search);
		
		return modelAndView;
	}
	
	@RequestMapping(value="listPurchase")
	public ModelAndView listPurchase(@ModelAttribute("search") Search search, HttpSession session) throws Exception{
		
		System.out.println("listPurchase : GET/POST");
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
		modelAndView.setViewName("/purchase/listPurchase.jsp");
		modelAndView.addObject("list",map.get("list"));
		modelAndView.addObject("resultPage", resultPage);
		modelAndView.addObject("search", search);
		
		return modelAndView;
	}
}
