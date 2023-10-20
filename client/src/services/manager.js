import req from "./config"

const manager = {
	createExpert: (username, email, password, specializations) => {
		return new Promise((resolve, reject) => {
			req.post("/manager/auth/createExpert", {
				username,
				email,
				password,
				details: {
					specializations,
				}
			})
				.then((res) => resolve(res.data))
				.catch((err) => reject(err.response.data));
		});
	},

	getChanges: () => {
		return new Promise((resolve, reject) => {
			req.get("/manager/changes")
				.then(res => resolve(res.data))
				.catch(err => reject(err.response.data))
		})
	},

	getExperts: () => {
		return new Promise((resolve, reject) => {
			req.get("/manager/tickets/experts")
			.then(res => resolve(res.data))
			.catch(err => reject((err.response.data)))
		})

	}
};

export default manager;
