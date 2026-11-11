package com.example.data.local

import kotlinx.coroutines.flow.Flow

/**
 * Repository abstracting access to local Room database for Product entities.
 */
class LocalProductRepository(private val productDao: ProductDao) {

    val allProducts: Flow<List<Product>> = productDao.getAllProducts()

    fun getProductById(id: Int): Flow<Product?> = productDao.getProductById(id)

    suspend fun findProductById(id: Int): Product? = productDao.findProductById(id)

    fun getProductsByCategory(category: String): Flow<List<Product>> =
        productDao.getProductsByCategory(category)

    fun getProductsByCity(city: String): Flow<List<Product>> =
        productDao.getProductsByCity(city)

    fun searchProducts(query: String): Flow<List<Product>> =
        productDao.searchProducts(query)

    suspend fun insertProduct(product: Product): Long = productDao.insertProduct(product)

    suspend fun insertAll(products: List<Product>) = productDao.insertAll(products)

    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)

    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)

    suspend fun deleteProductById(id: Int) = productDao.deleteProductById(id)

    suspend fun deleteAllProducts() = productDao.deleteAllProducts()
}
