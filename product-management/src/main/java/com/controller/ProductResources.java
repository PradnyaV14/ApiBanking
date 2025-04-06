package com.productManagement.controller;

import org.jboss.logging.Logger;

import com.productManagement.model.Product;
import com.productManagement.repository.ProductRepository;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * Resource file to perform CRUD operations. Can be tested via Postman.
 */
@Path("/products")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResources {

	@Inject
	ProductRepository productRepository;
	private static final Logger LOG = Logger.getLogger(ProductResources.class);

	/**
	 * To create a new product
	 * 
	 * @param product
	 * @return
	 */
	@POST
	public Uni<Response> createProduct(Product product) {
		LOG.info("Adding product: " + product);
		return product.persistAndFlush().replaceWith(Response.status(Response.Status.CREATED).entity(product).build());
	}

	/**
	 * To read the detailed list of all products.
	 * 
	 * @return
	 */
	@GET
	public Uni<List<Product>> getAllProducts() {
		LOG.info("Fetching all products");
		return productRepository.listAll().invoke(products -> LOG.info("Fetched products: " + products));
	}

	/**
	 * To read the list of all products and details of a specific product by id.
	 * 
	 * @param id
	 * @return
	 */
	@GET
	@Path("/{id}")
	public Uni<Response> getProductById(@PathParam("id") Long id) {
		LOG.info("Fetching product with id: " + id);
		return productRepository.findById(id).onItem().ifNotNull().transform(product -> {
			LOG.info("Fetched product based on id: " + product);
			return Response.ok(product).build();
		}).onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
	}

	/**
	 * To update the details of an existing product by id.
	 * 
	 * @param id
	 * @param product
	 * @return
	 */
	@PUT
	@Path("/{id}")
	public Uni<Response> updateProduct(@PathParam("id") Long id, Product product) {
		LOG.info("Updating product with id: " + id);
		return productRepository.findById(id).onItem().ifNotNull().transformToUni(existingProduct -> {
			existingProduct.name = product.name;
			existingProduct.description = product.description;
			existingProduct.price = product.price;
			existingProduct.quantity = product.quantity;
			LOG.info("Updated product: " + existingProduct);
			return existingProduct.persistAndFlush();
		}).replaceWith(Response.ok(product).build()).onItem().ifNull()
				.continueWith(Response.status(Response.Status.NOT_FOUND).build());
	}

	/**
	 * To delete a product by id.
	 * 
	 * @param id
	 * @return
	 */
	@DELETE
	@Path("/{id}")
	public Uni<Response> deleteProduct(@PathParam("id") Long id) {
		LOG.info("Deleting product with id: " + id);
		return productRepository.deleteById(id).map(deleted -> {
			if (deleted) {
				LOG.info("Deleted product with id: " + id);
				return Response.noContent().build();
			} else {
				LOG.warn("No product found with id: " + id);
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		});

	}

	/**
	 * To check stock availability by id.
	 * 
	 * @param id
	 * @param count
	 * @return
	 */
	@GET
	@Path("/{id}/stock")
	public Uni<Response> checkStock(@PathParam("id") Long id, @QueryParam("count") Integer count) {
		LOG.info("Checking stock for product id: " + id + " with count: " + count);
		return productRepository.findById(id).onItem().ifNotNull().transform(product -> {
			boolean available = product.quantity >= count;
			LOG.info("Stock available: " + available);
			return Response.ok(available).build();
		}).onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
	}

	/**
	 * To Retrieve all products in ascending order by price.
	 * 
	 * @return
	 */
	@GET
	@Path("/sorted")
	public Uni<List<Product>> getProductsSortedByPrice() {
		return productRepository.find("ORDER BY price ASC").list().invoke(products -> {
			   LOG.info("Fetching products sorted by price");
			products.forEach(product -> LOG.info(product.toString()));
		});
	}
}
