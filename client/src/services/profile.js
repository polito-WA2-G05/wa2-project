import req from "./config";

const profile = {
	editProfile: (name, surname, email) => {
		return new Promise((resolve, reject) => {
			req.put(`/customer/profiles/${email}`, {
				name, surname
			})
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		});
	},
}

export default profile;