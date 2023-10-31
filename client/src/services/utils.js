import req from "./config"
const utils = {
	getNotifications: () => {
		return new Promise((resolve, reject) => {
			req.get("/authenticated/notifications")
				.then(res => resolve(res.data))
				.catch(err => reject(err.response.data))
		})
	},

	deleteNotification: (id) => {
		return new Promise((resolve, reject) => {
			req.delete(`/authenticated/notifications/${id}`)
				.then((res) => resolve(res.data))
				.catch(err => reject(err.response.data))
		})
	},

	uploadAttachment: (role, ticketId, attachment) => {
		return new Promise((resolve, reject) => {
			req.post(`/${role.toLowerCase()}/tickets/${ticketId}/attachments`, attachment, {
				headers:
					{ "Content-Type": "multipart/form-data" }
			})
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data))
		})
	},

	getAttachments: (role, ticketId) => {
		return new Promise((resolve, reject) => {
			req.get(`/${role.toLowerCase()}/tickets/${ticketId}/attachments`)
				.then((res) => resolve(res.data))
				.catch(err => reject(err.response.data))
		})
	},

	getAttachment: (role, ticketId, fileName) => {
		return new Promise((resolve, reject) => {
			req.get(`/${role.toLowerCase()}/tickets/${ticketId}/attachments/${fileName}`, { responseType: "blob" })
				.then((res) => resolve(res.data))
				.catch(err => reject(err.response.data))
		})
	}
}

export default utils;