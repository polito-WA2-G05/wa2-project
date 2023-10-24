import req from "./config";

const profile = {
	getProfile: () => {
		return new Promise((resolve, reject) => {
			req.get(`/customer/profiles/me`)
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		});
	},

	editProfile: (name, surname) => {
		return new Promise((resolve, reject) => {
			req.put(`/customer/profiles/me`, {
				name, surname
			})
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		});
	},
}

export default profile;