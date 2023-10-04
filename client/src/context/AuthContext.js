import { createContext, useEffect, useState } from 'react'
import api from '../services/api';
import { Spinner } from 'react-bootstrap';

const AuthContext = createContext([{}, () => { }]);

const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);

   /* useEffect(() => {
        api.getUserInfo()
            .then((user) => {
                setUser({ user: { ...user }, loggedIn: true });
            })
            .catch(() => {
                setUser({ user: undefined, loggedIn: false });
            })
    }, []);

    if (user) {
        return (
            <AuthContext.Provider value={[user, setUser]}>
                {children}
            </AuthContext.Provider>
        );
    }*/

    // return (
    //     <div className='h-100 d-flex align-items-center justify-content-center'>
    //         <Spinner animation='border' variant='primary' className='opacity-25' />
    //     </div>
    // );
}

export { AuthContext, AuthProvider };
