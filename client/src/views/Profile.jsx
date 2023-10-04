import {useLocation, useParams, Navigate, useNavigate} from "react-router-dom";
// Imports
import {useEffect, useState} from "react";
// import {ProfileInfo} from "../components"

// Components
import {Button, Spinner} from "react-bootstrap";

// Services
import api from "../services/api";

// Hooks
import useNotification from "../hooks/useNotification";

const Profile = () => {
    const {email} = useParams()
    const location = useLocation()
    const notify = useNotification()
    const navigate = useNavigate()
    const {state} = location

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

    const handleNavigate = () => {
        navigate(`/profiles/${profile.email}/update`, {
            replace: true,
            state: {profile}
        })
    }

    if (!loading)
        return (
            <>
                {profile ? (
                    <>
                        <h2 className={"mb-4 fw-bold text-center"}>Profile Informations</h2>
                        {/*<ProfileInfo entity={profile}/>*/}

                        <Button onClick={handleNavigate} className={"mt-4"}>
                            Update profile
                        </Button>
                    </>
                ) : <Navigate to={"/profiles"} replace/>}
            </>
        )
    return <div className="d-flex justify-content-center align-items-center w-100">
        <Spinner animation='border' size='xl' as='span' role='status' aria-hidden='true' className='me-2'/>
        <h2>Loading...</h2>
    </div>
}

export default Profile