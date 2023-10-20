//Imports
import { useState, useEffect } from 'react'
import { Spinner } from 'react-bootstrap';

// Components
import { ExpertsTable } from '@components'

// Services
import api from '@services'

// Hooks
import { useNotification } from '@hooks';
const Experts = () => {
    const [experts, setExperts] = useState([]);
    const [loading, setLoading] = useState(true);
    const notify = useNotification()
    console.log(experts)

    useEffect(() => {
        if (loading) {
            api.manager.getExperts()
                .then(experts => {
                    setExperts(experts)
                })
                .catch(err => {
                    if (err.status === 404) {
                        setExperts([])
                    } else { notify.error(err.details ?? err) }
                })
                .finally(() => setLoading(false))
        }
    }, [])


    if (!loading)
        return (
            <div className='text-center'>
                {experts.length === 0 ? <h3 className={"fw-bold fs-2 mb-4"}>There are currently no tickets for the experts</h3> :
                    <>
                        <h3 className={"fw-bold mb-4 fs-2 text-center"}>Experts</h3>
                        <ExpertsTable experts={experts} />
                    </>
                }
            </div>
        )

    return <div className="d-flex justify-content-center align-items-center w-100">
        <Spinner animation='border' size='xl' as='span' role='status' aria-hidden='true' className='me-2' />
        <h2>Loading...</h2>
    </div>

}


export default Experts;