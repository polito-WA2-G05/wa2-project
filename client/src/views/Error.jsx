//Imports
import { Link } from 'react-router-dom';
import { Row, Button } from 'react-bootstrap';

// Icons
import { TbError404 } from 'react-icons/tb'

const ErrorView = () => {
    return (
        <Row className='p-5 m-5 flex-fill text-dark align-items-center'>
            <div className='d-flex flex-column align-items-center'>
                <TbError404 style={{ fontSize: '200px' }} className='me-3' />
                <h3 className='mb-0 fw-bold'>Page Not Found</h3>
                <div className='my-5'>
                    <Link to={'/'}>
                        <Button size='xs'>Back to home</Button>
                    </Link>
                </div>
            </div>
        </Row>
    );
}

export default ErrorView;