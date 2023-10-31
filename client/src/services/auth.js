import req from "./config";

const auth = {
	signup: (username, email, password, name, surname) => {
		return new Promise((resolve, reject) => {
			req.post("/anonymous/auth/signup", {
				username,
				email,
				password,
				details: {
					name,
					surname,
				},
			})
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		});
	},

	login: (username, password) => {
		return new Promise((resolve, reject) => {
			req.post("/anonymous/auth/login", { username, password })
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		});
	},

	logout: () => {
		return new Promise((resolve, reject) => {
			req.delete("/authenticated/auth/logout")
				.then(() => resolve())
				.catch((err) => reject(err.response.data));
		});
	},
};

export default auth;
