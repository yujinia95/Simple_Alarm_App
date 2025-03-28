package com.bcit.lab9yujinjeong.viewmodel


@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmDao: AlarmDao
) : ViewModel() {

}