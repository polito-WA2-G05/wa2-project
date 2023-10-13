// Imports
import {Col} from 'react-bootstrap'

// Components
import { EditProfileForm } from "@components/forms";

const EditProfile = () => {
    return (
        <div>
        <h1 className="fw-extrabold text-center">Edit Your Profile</h1>
        <Col xs={{ span: 12 }} lg={{ span: 4 }} className="mx-auto">
            <EditProfileForm />
        </Col>
    </div>
    );
}

export default EditProfile