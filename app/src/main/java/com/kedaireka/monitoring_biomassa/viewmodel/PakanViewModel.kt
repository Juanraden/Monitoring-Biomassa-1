package com.kedaireka.monitoring_biomassa.viewmodel

import androidx.lifecycle.*
import com.kedaireka.monitoring_biomassa.data.domain.PakanDomain
import com.kedaireka.monitoring_biomassa.database.dao.PakanDAO
import com.kedaireka.monitoring_biomassa.database.entity.Pakan
import com.kedaireka.monitoring_biomassa.util.EntityMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PakanViewModel @Inject constructor(
    private val pakanDAO: PakanDAO,
    private val pakanMapper: EntityMapper<Pakan, PakanDomain>
): ViewModel() {
    private val _loadedPakanId = MutableLiveData<Int>()
    val loadedPakanId: LiveData<Int> = _loadedPakanId

    fun getAll(): LiveData<List<PakanDomain>> = Transformations.map(pakanDAO.getAll().asLiveData()){list->
        list.map { pakanMapper.mapFromEntity(it) }
    }

    fun setPakanId(id: Int){
        _loadedPakanId.value = id
    }

    fun loadPakanData(id: Int): LiveData<PakanDomain>{
        return Transformations.map(pakanDAO.getById(id).asLiveData()){
            pakanMapper.mapFromEntity(it)
        }
    }

    fun insertPakan(jenis_pakan: String, deskripsi: String = ""){
        viewModelScope.launch {
            withContext(Dispatchers.IO){ pakanDAO.insertOne(Pakan(jenis_pakan = jenis_pakan, deskripsi = deskripsi)) }
        }
    }

    fun updatePakan(id: Int, nama: String, deskripsi: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                pakanDAO.updateOne(
                    Pakan(
                        pakan_id = id,
                        jenis_pakan = nama,
                        deskripsi = deskripsi
                    )
                )
            }
        }
    }

    fun isEntryValid(jenis_pakan: String, deskripsi: String): Boolean {
        return (jenis_pakan.isNotBlank() || deskripsi.isNotBlank())
    }
}