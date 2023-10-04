import axios from "axios";
const SERVER_URL = '/api';

const req = axios.create({
    baseURL: SERVER_URL
}) 

const api = {
     getAllProducts: () => {
         return new Promise((resolve,reject) =>
             req.get('/public/products')
                 .then(res => {resolve(res.data)})
                .catch(err => reject(err.response.data))
         )},

    getProductByEAN: (ean) => {
        return new Promise((resolve, reject) => {
            req.get(`/public/products/${ean}`)
                .then(res => resolve(res.data))
                .catch(err => reject(err.response.data));
        })
    },

    login: (username, password) => {
         return new Promise((resolve, reject) => {
             req.post("/anonymous/auth/login", {username, password})
                 .then(res => resolve(res.data))
                 .catch(err => reject(err.response.data))
         })
    }

    /*
    getProfileByEmail: (email) => {
         return new Promise((resolve, reject) => {
             req.get(`/customer/profiles/${email}`)
                 .then(res => resolve(res.data))
                 .catch(err => reject(err.response.data))
         })
    },

    insertProfile: (data) => {
         return new Promise((resolve, reject) => {
             req.post('/profiles', data)
                 .then(res => resolve(res.data))
                 .catch(err => reject(err.response.data))
         })
    },

    updateProfile: (email, data) => {
         return new Promise((resolve, reject) => {
             req.put(`/customer/profiles/${email}`, data)
                 .then(res => resolve(res.data))
                 .catch(err => reject(err.response.data))
         })
    },
    */
}

export default api;