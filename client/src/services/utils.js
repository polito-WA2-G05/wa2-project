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
				.then(() => resolve())
				.catch(err => reject(err.response.data))
		})
	}

}

export default utils;