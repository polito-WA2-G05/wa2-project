//Components
import {ProfileForm} from '../components/index'
import {Navigate, useLocation, useNavigate, useParams} from "react-router-dom";
import useNotification from "../hooks/useNotification";
import {useEffect, useState} from "react";
import api from "../services/api";
import {Spinner} from "react-bootstrap";

const UpdateProfile = () => {
    const {email} = useParams()
    const location = useLocation()
    const notify = useNotification()
    const {state} = location
    const navigate = useNavigate()

    const [profile, setProfile] = useState(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        if (loading) {
            if (!state?.profile) {
                api.getProfileByEmail(email)
                    .then((profile) => setProfile(profile))
                    .catch((err) => notify.error(err.detail))
                    .finally(() => setLoading(false))
            } else {
                setProfile(state.profile)
                setLoading(false)
            }
        }
    }, [])


    const handleSubmit = (values) => {
        api.updateProfile(email, values)
            .then((profile) => {
                navigate(`/profiles/${profile.email}`)
                notify.success(`Profile with id ${profile.id} successfully updated`)
            })
            .catch((err) => notify.error(err.detail))
    }


    if (!loading)
        return (
            <>
                {profile ? (
                    <>
                        <h1 className="mb-5 fs-1 fw-bold">Update profile</h1>
                        <ProfileForm handleSubmit={handleSubmit} initialValues={profile}/>
                    </>
                ) : <Navigate to={"/profiles"} replace/>}
            </>
        )

    return <div className="d-flex justify-content-center align-items-center w-100">
        <Spinner animation='border' size='xl' as='span' role='status' aria-hidden='true' className='me-2'/>
        <h2>Loading...</h2>
    </div>
}

export default UpdateProfile
