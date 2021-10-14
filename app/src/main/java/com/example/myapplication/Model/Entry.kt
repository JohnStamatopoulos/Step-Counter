package com.example.myapplication.Model

import android.util.Log


data class Entry(var id: Int, var date: String, var steps: Float){

    override fun toString(): String {
        return date.toString()
    }

    companion object MyComparator:Comparator<Entry>{
        var counter = 0

        private fun compareDays(date1Day: String, date2Day: String) : Int{
            if (date1Day == date2Day){
                return 0
            }
            else if (date1Day < date2Day) {
                return -1
            }
            else{
                return 1
            }
        }

        private fun compareMonths(date1Month: String, date2Month: String) : Int{
            if (date1Month == date2Month){
                return 0
            }
            else if (date1Month < date2Month) {
                return -1
            }
            else{
                return 1
            }
        }

        private fun compareYears(date1Year: String, date2Year: String) : Int{
            if (date1Year == date2Year){
                return 0
            }
            else if (date1Year < date2Year) {
                return -1
            }
            else{
                return 1
            }
        }

            override fun compare(entry1:Entry, other: Entry): Int {
                val date1Day = entry1.date.substring(0,2)
                val date1Month = entry1.date.substring(3,5)
                val date1Year = entry1.date.substring(6)

                val date2Day = other.date.substring(0,2)
                val date2Month = other.date.substring(3,5)
                val date2Year = other.date.substring(6)

                counter++
                Log.d("Entry Comparing...", "Date1:${date1Day} Month1:${date1Month} Year1:${date1Year} Counter=${counter}")

                if (compareYears(date1Year,date2Year) == 0){
                    if (compareMonths(date1Month, date2Month) == 0){
                        if (compareDays(date1Day, date2Day) == 0){
                            return 0
                        }
                        else if (compareDays(date1Day, date2Day) == -1){
                            return -1
                        }
                        else{
                            return 1
                        }
                    }
                    else if (compareMonths(date1Month, date2Month) == -1){
                        return -1
                    }
                    else{
                        return 1
                    }
                }

                else if (compareYears(date1Year,date2Year) == -1){
                    return -1
                }

                else{
                    return 1
                }
            }
        }

    }

