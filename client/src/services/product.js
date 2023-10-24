// Imports
import req from "./config";

const product = {
	getAllProducts: () => {
		return new Promise((resolve, reject) =>
			req.get("/public/products")
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data))
		);
	},

	getMyProducts: () => {
		return new Promise((resolve, reject) =>
			req.get(`/customer/purchases/products`)
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data))
		);
	},

	registerPurchase: (purchaseId) => {
		return new Promise((resolve, reject)=>{
			req.put(`/customer/purchases/${purchaseId}/register`, {purchaseId})
			.then((res) => resolve(res.data) )
			.catch( (err) => reject(err.response.data) )
		})
	},

	getProductByEAN: (ean) => {
		return new Promise((resolve, reject) => {
			req.get(`/public/products/${ean}`)
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		});
	},
};

export default product;
