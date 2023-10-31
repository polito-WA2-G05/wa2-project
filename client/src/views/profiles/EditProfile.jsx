// Imports
import {Col} from 'react-bootstrap'

// Components
import {EditProfileForm} from "@components/forms";

const EditProfile = () => {
    return <Col xs={12} lg={4}>
        <h1 className="fw-bold my-5 text-center">Edit your profile</h1>
        <EditProfileForm/>
    </Col>
}

export default EditProfile