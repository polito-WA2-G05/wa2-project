//Components
import {ProfileForm} from '../components/index'
import api from "../services/api";
import useNotification from "../hooks/useNotification";
import {useNavigate} from "react-router-dom";

const AddProfile = () => {
    const navigate = useNavigate()
    const notify = useNotification()

    const handleSubmit = (values) => {
        api.insertProfile(values)
            .then((profile) => {
                navigate(`/profiles/${profile.email}`)
                notify.success(`Profile with id ${profile.id} successfully updated`)
            })
            .catch((err) => notify.error(err.detail))
    }

    return (
        <>
            <h1 className="mb-5 fs-1 fw-bold">Create new profile</h1>
            <ProfileForm handleSubmit={handleSubmit}/>
        </>
    )
}

export default AddProfile
