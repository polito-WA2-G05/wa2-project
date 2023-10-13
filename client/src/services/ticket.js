import req from "./config";

const ticket = {
	getTicket: (role, ticketId) => {
		return new Promise((resolve, reject) => {
			req
				.get(`/${role}/tickets/${ticketId}`)
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		});
	},

	createTicket: (title, description, productEan, specializationId) => {
		return new Promise((resolve, reject) => {
			req
				.post("/customer/tickets", { title, description, productEan, specializationId })
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		});
	},

	getTickets: (role, product) => {
		// GET /api/{role}/tickets[?product={product}] 
		return new Promise ((resolve, reject) => {
			req.get(`/${role}/tickets${!product ? "": "?product="+product}`)
			.then((res) => resolve(res.data))
			.catch((err) => reject(err.response.data));
		})
	}
};

export default ticket;
