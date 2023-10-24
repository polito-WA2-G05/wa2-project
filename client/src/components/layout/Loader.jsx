import { Spinner } from "react-bootstrap"

const Loader = () => {
    return <div className="position-absolute d-flex align-items-center justify-content-center h-100 w-100">
        <Spinner size='xl' as='span' role='status' aria-hidden='true' className='opacity-50' variant="primary" />
    </div>
}

export default Loader