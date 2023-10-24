import axios from "axios";

const SERVER_URL = "/api"
export const SOCKET_URL = "http://localhost:8080/ws"

const req = axios.create({
    baseURL: SERVER_URL,
});

const refreshToken = (refreshToken) => {
    return new Promise((resolve, reject) => {
        axios.post(`${SERVER_URL}/public/auth/refresh-token`, { refreshToken })
            .then((res) => resolve(res.data))
            .catch((err) => reject(err.response.data));
    });
}

req.interceptors.request.use(async (config) => {
    const session = JSON.parse(sessionStorage.getItem('session'))
    if (session) {
        config.headers = {
            Authorization: `Bearer ${session.details.accessToken}`
        }
    }

    return config
}, (err) => Promise.reject(err))

req.interceptors.response.use(
    (res) => res,
    async (err) => {
        const session = JSON.parse(sessionStorage.getItem('session'))
        const originalRequest = err.config

        if (session && err.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                const res = await refreshToken(session.details.refreshToken)

                originalRequest.headers = {
                    ...originalRequest.headers,
                    Authorization: `Bearer ${res.accessToken}`,
                }
                session.details.accessToken = res.accessToken
                session.details.refreshToken = res.refreshToken
                sessionStorage.setItem('session', JSON.stringify(session))

                return req.request(originalRequest);
            } catch {
                sessionStorage.removeItem('session')
            }
        }

        return Promise.reject(err);
    })

export default req