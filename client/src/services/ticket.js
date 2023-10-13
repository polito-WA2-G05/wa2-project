import req from "./config";

const ticket = {
	getTicket: (ticketId) => {
		return new Promise((resolve, reject) => {
			req
				.get(`/authenticated/tickets/${ticketId}`)
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
	
	getTicketByProductEAN: (ean) => {
		return new Promise((resolve, reject) => {
			req.get(`/manager/tickets?product=${ean}`)
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		});
	}
};

export default ticket;
