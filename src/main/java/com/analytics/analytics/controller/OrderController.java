package com.analytics.analytics.controller;

import com.analytics.analytics.dao.ProductRepository;
import com.analytics.analytics.entity.Buyer;
import com.analytics.analytics.entity.Order;

import com.analytics.analytics.entity.Product;
import com.analytics.analytics.services.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("orderservice")
public class OrderController {
	
	@Autowired
	private IOrderService service;

	@Autowired
	private ProductServiceImpl productService;
	
	@GetMapping("orders")
	public ResponseEntity<List<Order>> getOrders(){
		
		List<Order> orders = service.getOrders();
		return new ResponseEntity<List<Order>>(orders, HttpStatus.OK);
		
	}
	
	@GetMapping("orders/{id}")
	public ResponseEntity<Order> getOrder(@PathVariable("id") Integer id){
		Order order = service.getOrder(id);
		return new ResponseEntity<Order>(order, HttpStatus.OK);
	}
	
	@PostMapping("orders")
	public ResponseEntity<Order> createOrder(@RequestBody Order order) {

		Buyer buyer = order.getBuyer();
		String firstName = buyer.getFirstName();
		String email = buyer.getEmail();
		String dateOfBirth = buyer.getDateOfBirth();

		Order item = service.createOrder(order);
		Boolean isSuccess = productService.addProductMetric(firstName, email, dateOfBirth);

		return new ResponseEntity<Order>(item, HttpStatus.OK);
		
	}
	
	@PutMapping("orders/{id}")
	public ResponseEntity<Order> updateOrder(@PathVariable("id") int id, @RequestBody Order order){
		
		Order b = service.updateOrder(id, order);
		return new ResponseEntity<Order>(b, HttpStatus.OK);
	}
	
	@DeleteMapping("orders/{id}")
	public ResponseEntity<String> deleteOrder(@PathVariable("id") int id){
		boolean isDeleted = service.deleteOrder(id);
		if(isDeleted){
			String responseContent = "Order has been deleted successfully";
			return new ResponseEntity<String>(responseContent,HttpStatus.OK);
		}
		String error = "Error while deleting order from database";
		return new ResponseEntity<String>(error,HttpStatus.INTERNAL_SERVER_ERROR);
	}

}

class ProductServiceImpl {

	@Autowired
	private ProductRepository productRepository;

	public Boolean addProductMetric(String name, String email, String dateOfBirth) {
		Product product = new Product();
		product.setBuyerEmail(email);
		product.setName(name);

		Product productResult = this.saveProduct(product);
		if (productResult != null) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public Product saveProduct(Product product) {
		return productRepository.saveProduct(product);
	}

	public Product getProductById(String productId) {
		return productRepository.getProductById(productId);
	}

	public String updateProduct(String productId, Product product) {
		return productRepository.updateProduct(productId, product);
	}
}

