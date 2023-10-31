//Imports
import {useContext, useEffect, useState} from 'react'

// Components
import {Loader} from '@components/layout'
import {ExpertsTable} from '@components'

// Services
import api from '@services'

import {SessionContext} from '@contexts';
import {Col} from "react-bootstrap";

const Experts = () => {
    const [experts, setExperts] = useState([]);
    const [loading, setLoading] = useState(true);

    const {onError} = useContext(SessionContext)

    useEffect(() => {
        if (loading) {
            api.manager.getExperts()
                .then(experts => setExperts(experts))
                .catch(onError)
                .finally(() => setLoading(false))
        }
    }, []) // eslint-disable-line


    if (!loading)
        return (
            <>
                {experts.length === 0 ? <h4 className={"text-center fw-bold"}>No experts have been found</h4> :
                    <Col xs={12} lg={11} className='text-center align-self-start'>
                        <h1 className={"fw-bold my-5"}>Experts</h1>
                        <ExpertsTable experts={experts}/>
                    </Col>
                }
            </>
        )

    return <Loader/>
}

export default Experts;