import req from "./config";

// Utils
import { Role } from '@utils'

const ticket = {
	getTicket: (role, ticketId) => {
		return new Promise((resolve, reject) => {
			req
				.get(`/${role}/tickets/${ticketId}`)
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		});
	},

	getSpecializations: () => {
		return new Promise((resolve, reject) => {
			req.get(`/authenticated/specializations`)
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		})
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
		return new Promise((resolve, reject) => {
			req.get(`/${role.toLowerCase()}/tickets${!product ? "" : "?product=" + product}`)
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		})
	},

	cancelTicket: (ticketId) => {
		return new Promise((resolve, reject) => {
			req.patch(`/customer/tickets/${ticketId}/cancel`)
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		})
	},

	closeTicket: (role, ticketId) => {
		return new Promise((resolve, reject) => {
			req.patch(`/${role.toLowerCase()}/tickets/${ticketId}/close`)
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		})
	},

	reopenTicket: (ticketId) => {
		return new Promise((resolve, reject) => {
			req.patch(`/customer/tickets/${ticketId}/reopen`)
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		})
	},

	inProgressTicket: (priorityLevel, ticketId) => {
		return new Promise((resolve, reject) => {
			req.patch(`/manager/tickets/${ticketId}/start`, { priorityLevel })
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		})
	},

	stopTicket: (ticketId) => {
		return new Promise((resolve, reject) => {
			req.patch(`/expert/tickets/${ticketId}/stop`)
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		})
	},

	resolveTicket: (role, ticketId, description) => {
		return new Promise((resolve, reject) => {
			const body = role === Role.MANAGER ? { description } : {}
			req.patch(`/${role.toLowerCase()}/tickets/${ticketId}/resolve`, body)
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		})
	},

	getChanges: () => {
		return new Promise((resolve, reject) => {
			req.get("/manager/tickets/changes")
				.then(res => resolve(res.data))
				.catch(err => reject(err.response.data))
		})
	},

	sendSurvey: (ticketId, serviceValuation, professionality, comment) => {
		return new Promise((resolve, reject) => {
			req.post(`/customer/tickets/${ticketId}/survey`, {
				serviceValuation, professionality, comment
			})
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		})
	},

	getMessagesHistory: (role, ticketId) => {
		return new Promise((resolve, reject) => {
			req.get(`/${role}/tickets/${ticketId}/messages/history`)
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		})
	}
};

export default ticket;
